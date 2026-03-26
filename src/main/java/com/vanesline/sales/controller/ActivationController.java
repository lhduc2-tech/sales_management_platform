package com.vanesline.sales.controller;

import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.SetPasswordRequest;
import com.vanesline.sales.service.contract.ActivationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("${application.path.base.public}/activate")
public class ActivationController {

    private final ActivationService activationService;

    public ActivationController(ActivationService activationService) {
        this.activationService = activationService;
    }

    @GetMapping
    public ResponseEntity<Void> validateToken(@RequestParam String token) {
        try {
            activationService.activateAccount(token);
            // Redirect to set password page
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/sales/set-password.html?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)))
                    .build();
        } catch (Exception e) {
            // Redirect to error page or handle error
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/sales/activation-error.html?message=" + errorMessage))
                    .build();
        }
    }

    @PostMapping("/set-password")
    public ResponseEntity<LoginResponse> setPasswordAndActivate(@Valid @RequestBody SetPasswordRequest request) {
        LoginResponse response = activationService.activateAccountWithPassword(request);
        return ResponseEntity.ok(response);
    }
}
