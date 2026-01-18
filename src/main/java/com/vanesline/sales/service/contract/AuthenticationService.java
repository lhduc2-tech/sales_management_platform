package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.auth.LoginRequest;
import com.vanesline.sales.dto.auth.LoginResponse;

public interface AuthenticationService {

    /**
     * Authenticate user with username and password
     * 
     * @param request login credentials
     * @return login response with JWT token
     */
    LoginResponse login(LoginRequest request);

    /**
     * Logout user by blacklisting the token
     * 
     * @param token JWT token to invalidate
     */
    void logout(String token);
}
