package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.staff.StaffRequest;
import com.vanesline.sales.dto.staff.StaffResponse;
import com.vanesline.sales.dto.staff.StaffSearchCriteria;
import com.vanesline.sales.entity.Staff;
import com.vanesline.sales.exception.DataConflictException;
import com.vanesline.sales.repository.StaffRepository;
import com.vanesline.sales.service.contract.StaffService;
import com.vanesline.sales.specification.StaffSpecification;
import com.vanesline.sales.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public StaffResponse create(StaffRequest request) {
        // Check for duplicate email
        if (StringUtils.hasText(request.getEmail()) && staffRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Email already exists: " + request.getEmail());
        }

        // Check for duplicate phone
        if (StringUtils.hasText(request.getPhoneNumber())
                && staffRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DataConflictException("Phone number already exists: " + request.getPhoneNumber());
        }

        Staff staff = Staff.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .shopId(request.getShopId())
                .build();

        staff.setCreatedBy(request.getCreatedBy());
        staff.setUpdatedBy(request.getUpdatedBy());

        staff = staffRepository.save(staff);
        return toResponse(staff);
    }

    @Override
    public StaffResponse update(UUID id, StaffRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id: " + id));

        // Check for duplicate email (excluding current staff)
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(staff.getEmail())) {
            if (staffRepository.existsByEmail(request.getEmail())) {
                throw new DataConflictException("Email already exists: " + request.getEmail());
            }
        }

        // Check for duplicate phone (excluding current staff)
        if (StringUtils.hasText(request.getPhoneNumber()) && !request.getPhoneNumber().equals(staff.getPhoneNumber())) {
            if (staffRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DataConflictException("Phone number already exists: " + request.getPhoneNumber());
            }
        }

        staff.setFullName(request.getFullName());
        staff.setEmail(request.getEmail());
        staff.setPhoneNumber(request.getPhoneNumber());
        staff.setUpdatedBy(request.getUpdatedBy());
        staff.setShopId(request.getShopId());

        staff = staffRepository.save(staff);
        return toResponse(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getById(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id: " + id));
        return toResponse(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StaffResponse> search(StaffSearchCriteria criteria, Pageable pageable) {
        Page<Staff> page = staffRepository.findAll(
                StaffSpecification.withCriteria(criteria),
                pageable);

        Page<StaffResponse> responsePage = page.map(this::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        if (!staffRepository.existsById(id)) {
            throw new EntityNotFoundException("Staff not found with id: " + id);
        }
        staffRepository.deleteById(id);
    }

    private StaffResponse toResponse(Staff staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .shopId(staff.getShopId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .shopId(staff.getShopId())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .createdBy(staff.getCreatedBy())
                .updatedBy(staff.getUpdatedBy())
                .build();
    }
}
