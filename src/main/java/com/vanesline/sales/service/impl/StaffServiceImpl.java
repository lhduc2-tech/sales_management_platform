package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.staff.StaffRequest;
import com.vanesline.sales.dto.staff.StaffResponse;
import com.vanesline.sales.dto.staff.StaffSearchCriteria;
import com.vanesline.sales.entity.Staff;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.exception.DataConflictException;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.StaffRepository;
import com.vanesline.sales.repository.UserRepository;
import com.vanesline.sales.service.contract.ActivationService;
import com.vanesline.sales.service.contract.EmailService;
import com.vanesline.sales.service.contract.StaffService;
import com.vanesline.sales.specification.StaffSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final ActivationService activationService;
    private final EmailService emailService;

    @Value("${app.activation.base-url}")
    private String activationBaseUrl;

    public StaffServiceImpl(StaffRepository staffRepository,
            UserRepository userRepository,
            ActivationService activationService,
            EmailService emailService) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.activationService = activationService;
        this.emailService = emailService;
    }

    @Override
    public StaffResponse create(StaffRequest request) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String userName = authentication.getName();

        // Check for duplicate email in User table
        if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Email already exists: " + request.getEmail());
        }

        // Check for duplicate phone in User table
        if (StringUtils.hasText(request.getPhoneNumber())
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DataConflictException("Phone number already exists: " + request.getPhoneNumber());
        }

        // Generate username from email (part before @)
        String username = generateUsernameFromEmail(request.getEmail());

        // Create User with DEACTIVE status
        User user = User.builder()
                .username(username)
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .fullName(request.getFullName())
                .passwordHash("") // Empty password, will be set during activation
                .status("DEACTIVE")
                .isSystem(false)
                .authProvider("LOCAL")
                .shopId(request.getShopId())
                .build();

        user.setCreatedBy(userName);
        user.setUpdatedBy(userName);

        user = userRepository.save(user);

        // Create Staff linked to User
        Staff staff = Staff.builder()
                .shopId(request.getShopId())
                .userId(user.getId())
                .status("INACTIVE")
                .position(request.getPosition())
                .build();

        staff.setCreatedBy(userName);
        staff.setUpdatedBy(userName);

        staff = staffRepository.save(staff);

        // Create activation token
        String token = activationService.createActivationToken(user.getId());

        // Build activation link
        String activationLink = activationBaseUrl + "/activate?token=" + token;

        // Send activation email
        emailService.sendActivationEmail(request.getEmail(), request.getFullName(), activationLink);

        return toResponse(staff, user);
    }

    @Override
    public StaffResponse update(UUID id, StaffRequest request) {
        Staff staff = staffRepository.findByIdWithUser(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id: " + id));

        User user = staff.getUser();
        if (user == null) {
            user = userRepository.findById(staff.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found for staff: " + id));
        }

        // Check for duplicate email (excluding current user)
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DataConflictException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // Check for duplicate phone (excluding current user)
        if (StringUtils.hasText(request.getPhoneNumber()) && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DataConflictException("Phone number already exists: " + request.getPhoneNumber());
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        // Update staff fields
        staff.setShopId(request.getShopId());
        if (request.getPosition() != null) {
            staff.setPosition(request.getPosition());
        }

        // Update user fields
        user.setFullName(request.getFullName());
        user.setShopId(request.getShopId());

        userRepository.save(user);
        staff = staffRepository.save(staff);

        return toResponse(staff, user);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getById(UUID id) {
        Staff staff = staffRepository.findByIdWithUser(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id: " + id));

        User user = staff.getUser();
        if (user == null && staff.getUserId() != null) {
            user = userRepository.findById(staff.getUserId()).orElse(null);
        }

        return toResponse(staff, user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StaffResponse> search(StaffSearchCriteria criteria, Pageable pageable) {
        Page<Staff> page = staffRepository.findAll(
                StaffSpecification.withCriteria(criteria),
                pageable);

        Page<StaffResponse> responsePage = page.map(staff -> {
            User user = null;
            if (staff.getUserId() != null) {
                user = userRepository.findById(staff.getUserId()).orElse(null);
            }
            return toResponse(staff, user);
        });

        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id: " + id));

        // Delete associated user if exists
        if (staff.getUserId() != null) {
            userRepository.deleteById(staff.getUserId());
        }

        staffRepository.deleteById(id);
    }

    private String generateUsernameFromEmail(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }

    private StaffResponse toResponse(Staff staff, User user) {
        StaffResponse.StaffResponseBuilder builder = StaffResponse.builder()
                .id(staff.getId())
                .shopId(staff.getShopId())
                .userId(staff.getUserId())
                .status(staff.getStatus())
                .position(staff.getPosition())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .createdBy(staff.getCreatedBy())
                .updatedBy(staff.getUpdatedBy());

        if (user != null) {
            builder.fullName(user.getFullName())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .userStatus(user.getStatus());
        }

        return builder.build();
    }
}
