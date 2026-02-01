package com.vanesline.sales.controller;

import com.vanesline.sales.dto.auth.ActivationResponse;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.SetPasswordRequest;
import com.vanesline.sales.service.contract.ActivationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${application.path.base.public}/activate")
public class ActivationController {

    private final ActivationService activationService;

    public ActivationController(ActivationService activationService) {
        this.activationService = activationService;
    }

    @GetMapping
    public ResponseEntity<ActivationResponse> validateToken(@RequestParam String token) {
        ActivationResponse response = activationService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/set-password")
    public ResponseEntity<LoginResponse> setPasswordAndActivate(@Valid @RequestBody SetPasswordRequest request) {
        LoginResponse response = activationService.activateAccountWithPassword(request);
        return ResponseEntity.ok(response);
    }
}
