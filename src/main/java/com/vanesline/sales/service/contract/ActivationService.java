package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.auth.ActivationResponse;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.SetPasswordRequest;

import java.util.UUID;

public interface ActivationService {

    String createActivationToken(UUID userId);

    ActivationResponse validateToken(String token);

    LoginResponse activateAccountWithPassword(SetPasswordRequest request);
}
