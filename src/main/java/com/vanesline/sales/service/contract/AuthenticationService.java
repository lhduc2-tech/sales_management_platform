package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.auth.GoogleAuthRequest;
import com.vanesline.sales.dto.auth.LoginRequest;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.RegisterRequest;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);

    LoginResponse googleAuth(GoogleAuthRequest request);

    void logout(String token);
}
