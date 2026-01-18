package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.auth.LoginRequest;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.entity.Permission;
import com.vanesline.sales.entity.Role;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.repository.UserRepository;
import com.vanesline.sales.sercurity.jwt.JwtTokenService;
import com.vanesline.sales.sercurity.jwt.TokenBlacklistService;
import com.vanesline.sales.service.contract.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtTokenService jwtTokenService,
            TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
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

            // Generate JWT token
            String token = jwtTokenService.generateToken(user);

            // Extract roles and permissions for response
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
                    .expiresIn(jwtTokenService.getJwtExpiration() / 1000) // Convert to seconds
                    .userId(user.getId().toString())
                    .username(user.getUsername())
                    .roles(roles)
                    .permissions(permissions)
                    .build();

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password", e);
        }
    }

    @Override
    public void logout(String token) {
        tokenBlacklistService.blacklist(token);
    }
}
