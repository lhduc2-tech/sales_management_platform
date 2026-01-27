package com.vanesline.sales.controller;

import com.vanesline.sales.dto.auth.GoogleAuthRequest;
import com.vanesline.sales.dto.auth.GoogleRegisterRequest;
import com.vanesline.sales.dto.auth.LoginRequest;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.RegisterRequest;
import com.vanesline.sales.service.contract.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("${application.path.base.public}/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("${application.path.base.public}/auth/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("${application.path.base.public}/auth/google")
    public ResponseEntity<LoginResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        LoginResponse response = authenticationService.googleAuth(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("${application.path.base.public}/auth/google/register")
    public ResponseEntity<LoginResponse> googleRegister(@Valid @RequestBody GoogleRegisterRequest request) {
        LoginResponse response = authenticationService.googleRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("${application.path.base.private}/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            authenticationService.logout(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
