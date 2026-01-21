package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.auth.GoogleAuthRequest;
import com.vanesline.sales.dto.auth.LoginRequest;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.RegisterRequest;
import com.vanesline.sales.entity.Permission;
import com.vanesline.sales.entity.Role;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.entity.UserRole;
import com.vanesline.sales.exception.DataConflictException;
import com.vanesline.sales.repository.RoleRepository;
import com.vanesline.sales.repository.UserRepository;
import com.vanesline.sales.sercurity.GoogleTokenVerifier;
import com.vanesline.sales.sercurity.jwt.JwtTokenService;
import com.vanesline.sales.sercurity.jwt.TokenBlacklistService;
import com.vanesline.sales.service.contract.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String DEFAULT_ROLE_CODE = "ADMIN";
    private static final String AUTH_PROVIDER_LOCAL = "LOCAL";
    private static final String AUTH_PROVIDER_GOOGLE = "GOOGLE";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenService jwtTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtTokenService jwtTokenService,
            TokenBlacklistService tokenBlacklistService,
            PasswordEncoder passwordEncoder,
            GoogleTokenVerifier googleTokenVerifier) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenService = jwtTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.passwordEncoder = passwordEncoder;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // Load user with roles and permissions
            User user = userRepository.findByUsernameWithRolesAndPermissions(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            return buildLoginResponse(user);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password", e);
        }
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Check for existing username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DataConflictException("Username already exists: " + request.getUsername());
        }

        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Email already exists: " + request.getEmail());
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DataConflictException("Phone number already exists: " + request.getPhoneNumber());
        }

        // Get default ADMIN role
        Role adminRole = roleRepository.findByCode(DEFAULT_ROLE_CODE)
                .orElseThrow(() -> new RuntimeException(
                        "Default role ADMIN not found. Please ensure roles are properly seeded."));

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .status("ACTIVE")
                .isSystem(false)
                .authProvider(AUTH_PROVIDER_LOCAL)
                .lastLoginAt(LocalDateTime.now())
                .build();

        // Save user first to get the ID
        user = userRepository.save(user);

        // Assign ADMIN role
        UserRole userRole = UserRole.builder()
                .id(new UserRole.UserRoleId(user.getId(), adminRole.getId()))
                .user(user)
                .role(adminRole)
                .assignedAt(OffsetDateTime.now())
                .assignedBy("SYSTEM")
                .build();
        user.getUserRoles().add(userRole);
        user = userRepository.save(user);

        // Reload user with roles and permissions for JWT generation
        User userWithRoles = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after creation"));

        return buildLoginResponse(userWithRoles);
    }

    @Override
    @Transactional
    public LoginResponse googleAuth(GoogleAuthRequest request) {
        // Verify Google token and extract user info
        GoogleTokenVerifier.GoogleUserInfo googleUserInfo = googleTokenVerifier.verify(request.getIdToken());

        // Try to find existing user by Google ID
        Optional<User> existingUserByGoogleId = userRepository.findByGoogleId(googleUserInfo.getGoogleId());

        if (existingUserByGoogleId.isPresent()) {
            // Existing Google user - login
            User user = existingUserByGoogleId.get();
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            User userWithRoles = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return buildLoginResponse(userWithRoles);
        }

        // Check if email is already used by a local account
        Optional<User> existingUserByEmail = userRepository.findByEmail(googleUserInfo.getEmail());
        if (existingUserByEmail.isPresent()) {
            // Link Google account to existing local account
            User user = existingUserByEmail.get();
            user.setGoogleId(googleUserInfo.getGoogleId());
            if (AUTH_PROVIDER_LOCAL.equals(user.getAuthProvider())) {
                user.setAuthProvider(AUTH_PROVIDER_LOCAL); // Keep as LOCAL if it was originally local
            }
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            User userWithRoles = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return buildLoginResponse(userWithRoles);
        }

        // New user - register with Google
        Role adminRole = roleRepository.findByCode(DEFAULT_ROLE_CODE)
                .orElseThrow(() -> new RuntimeException(
                        "Default role ADMIN not found. Please ensure roles are properly seeded."));

        // Generate username from email (before @)
        String baseUsername = googleUserInfo.getEmail().split("@")[0];
        String username = generateUniqueUsername(baseUsername);

        // Create new user with Google authentication
        User user = User.builder()
                .username(username)
                .email(googleUserInfo.getEmail())
                .passwordHash("") // No password for Google-only users
                .status("ACTIVE")
                .isSystem(false)
                .googleId(googleUserInfo.getGoogleId())
                .authProvider(AUTH_PROVIDER_GOOGLE)
                .lastLoginAt(LocalDateTime.now())
                .build();

        // Save user first to get the ID
        user = userRepository.save(user);

        // Assign ADMIN role
        UserRole userRole = UserRole.builder()
                .id(new UserRole.UserRoleId(user.getId(), adminRole.getId()))
                .user(user)
                .role(adminRole)
                .assignedAt(OffsetDateTime.now())
                .assignedBy("SYSTEM")
                .build();
        user.getUserRoles().add(userRole);
        user = userRepository.save(user);

        // Reload user with roles and permissions
        User userWithRoles = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after creation"));

        return buildLoginResponse(userWithRoles);
    }

    @Override
    public void logout(String token) {
        tokenBlacklistService.blacklist(token);
    }

    /**
     * Build LoginResponse from User entity
     */
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

    /**
     * Generate unique username by appending numbers if necessary
     */
    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }
}
