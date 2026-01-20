package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.auth.GoogleAuthRequest;
import com.vanesline.sales.dto.auth.LoginRequest;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.RegisterRequest;

public interface AuthenticationService {

    /**
     * Authenticate user with username and password
     * 
     * @param request login credentials
     * @return login response with JWT token
     */
    LoginResponse login(LoginRequest request);

    /**
     * Register a new user with username, email, and password
     * 
     * @param request registration details
     * @return login response with JWT token
     */
    LoginResponse register(RegisterRequest request);

    /**
     * Authenticate or register user with Google OAuth
     * 
     * @param request Google ID token
     * @return login response with JWT token
     */
    LoginResponse googleAuth(GoogleAuthRequest request);

    /**
     * Logout user by blacklisting the token
     * 
     * @param token JWT token to invalidate
     */
    void logout(String token);
}
