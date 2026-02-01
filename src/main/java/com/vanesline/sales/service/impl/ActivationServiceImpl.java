package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.auth.ActivationResponse;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.SetPasswordRequest;
import com.vanesline.sales.entity.ActivationToken;
import com.vanesline.sales.entity.Permission;
import com.vanesline.sales.entity.Role;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.entity.UserRole;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.ActivationTokenRepository;
import com.vanesline.sales.repository.RoleRepository;
import com.vanesline.sales.repository.UserRepository;
import com.vanesline.sales.sercurity.jwt.JwtTokenService;
import com.vanesline.sales.service.contract.ActivationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivationServiceImpl implements ActivationService {

        private static final String SALES_ROLE_CODE = "SALES";

        private final ActivationTokenRepository activationTokenRepository;
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenService jwtTokenService;

        @Value("${app.activation.token-expiry-hours:24}")
        private int tokenExpiryHours;

        public ActivationServiceImpl(ActivationTokenRepository activationTokenRepository,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        JwtTokenService jwtTokenService) {
                this.activationTokenRepository = activationTokenRepository;
                this.userRepository = userRepository;
                this.roleRepository = roleRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtTokenService = jwtTokenService;
        }

        @Override
        @Transactional
        public String createActivationToken(UUID userId) {
                // Delete any existing unused tokens for this user
                activationTokenRepository.deleteByUserId(userId);

                String token = UUID.randomUUID().toString();

                ActivationToken activationToken = ActivationToken.builder()
                                .token(token)
                                .userId(userId)
                                .expiresAt(OffsetDateTime.now().plusHours(tokenExpiryHours))
                                .used(false)
                                .build();

                activationTokenRepository.save(activationToken);
                return token;
        }

        @Override
        @Transactional(readOnly = true)
        public ActivationResponse validateToken(String token) {
                ActivationToken activationToken = activationTokenRepository.findByTokenAndUsedFalse(token)
                                .orElse(null);

                if (activationToken == null) {
                        return ActivationResponse.builder()
                                        .valid(false)
                                        .message("Token không hợp lệ hoặc đã được sử dụng")
                                        .build();
                }

                if (activationToken.isExpired()) {
                        return ActivationResponse.builder()
                                        .valid(false)
                                        .message("Token đã hết hạn")
                                        .build();
                }

                User user = userRepository.findById(activationToken.getUserId())
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));

                // Get full name from user
                String fullName = user.getFullName() != null ? user.getFullName() : user.getUsername();

                return ActivationResponse.builder()
                                .valid(true)
                                .email(user.getEmail())
                                .fullName(fullName)
                                .message("Token hợp lệ")
                                .build();
        }

        @Override
        @Transactional
        public LoginResponse activateAccountWithPassword(SetPasswordRequest request) {
                // Validate passwords match
                if (!request.getPassword().equals(request.getConfirmPassword())) {
                        throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
                }

                // Find and validate token
                ActivationToken activationToken = activationTokenRepository.findByTokenAndUsedFalse(request.getToken())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Token không hợp lệ hoặc đã được sử dụng"));

                if (activationToken.isExpired()) {
                        throw new IllegalArgumentException("Token đã hết hạn");
                }

                // Get user
                User user = userRepository.findById(activationToken.getUserId())
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));

                // Update password and activate
                user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                user.setStatus("ACTIVE");

                // Assign SALES role
                Role salesRole = roleRepository.findByCode(SALES_ROLE_CODE)
                                .orElseThrow(
                                                () -> new RuntimeException(
                                                                "Role SALES not found. Please ensure roles are properly seeded."));

                UserRole userRole = UserRole.builder()
                                .id(new UserRole.UserRoleId(user.getId(), salesRole.getId()))
                                .user(user)
                                .role(salesRole)
                                .assignedAt(OffsetDateTime.now())
                                .assignedBy("SYSTEM")
                                .build();
                user.getUserRoles().add(userRole);

                userRepository.save(user);

                // Mark token as used
                activationToken.setUsed(true);
                activationTokenRepository.save(activationToken);

                // Reload user with roles for JWT generation
                User userWithRoles = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername())
                                .orElseThrow(() -> new RuntimeException("User not found after activation"));

                return buildLoginResponse(userWithRoles);
        }

        private LoginResponse buildLoginResponse(User user) {
                String token = jwtTokenService.generateToken(user);

                List<String> roles = user.getRoles().stream()
                                .map(Role::getCode)
                                .collect(Collectors.toList());

                List<String> permissions = user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(Permission::getCode)
                                .distinct()
                                .collect(Collectors.toList());

                return LoginResponse.builder()
                                .accessToken(token)
                                .tokenType("Bearer")
                                .expiresIn(jwtTokenService.getJwtExpiration() / 1000)
                                .userId(user.getId().toString())
                                .username(user.getUsername())
                                .roles(roles)
                                .permissions(permissions)
                                .build();
        }
}
