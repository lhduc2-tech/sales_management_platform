package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.auth.GoogleAuthRequest;
import com.vanesline.sales.dto.auth.LoginRequest;
import com.vanesline.sales.dto.auth.LoginResponse;
import com.vanesline.sales.dto.auth.RegisterRequest;
import com.vanesline.sales.entity.Role;
import com.vanesline.sales.entity.Shop;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.exception.DataConflictException;
import com.vanesline.sales.repository.RoleRepository;
import com.vanesline.sales.repository.ShopRepository;
import com.vanesline.sales.repository.StaffRepository;
import com.vanesline.sales.repository.UserRepository;
import com.vanesline.sales.sercurity.GoogleTokenVerifier;
import com.vanesline.sales.sercurity.jwt.JwtTokenService;
import com.vanesline.sales.sercurity.jwt.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GoogleTokenVerifier googleTokenVerifier;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private Role role;
    private Shop shop;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        role = Role.builder().id(UUID.randomUUID()).code("ADMIN").build();
        shop = Shop.builder().id(UUID.randomUUID()).shopName("Test Shop").build();
        
        user = User.builder()
                .username("testuser")
                .passwordHash("encoded_password")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .status("ACTIVE")
                .build();
        user.setId(userId);
        user.addRole(role);
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("testuser", "password");
        Authentication authentication = mock(Authentication.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsernameWithRolesAndPermissions("testuser")).thenReturn(Optional.of(user));
        when(jwtTokenService.generateToken(any(User.class), any())).thenReturn("mocked-jwt-token");
        when(jwtTokenService.getJwtExpiration()).thenReturn(3600000L);

        LoginResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
        assertEquals("testuser", response.getUsername());
        verify(userRepository).save(any(User.class)); // Verifies last login update
    }

    @Test
    void login_ThrowsException_WhenBadCredentials() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(RuntimeException.class, () -> authenticationService.login(request));
        verify(userRepository, never()).findByUsernameWithRolesAndPermissions(anyString());
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("newuser", "password", "new@example.com", "0987654321", "New Shop", "Address", "RETAIL");
        
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(roleRepository.findByCode("ADMIN")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
        
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(shopRepository.save(any(Shop.class))).thenReturn(shop);
        when(userRepository.findByUsernameWithRolesAndPermissions(anyString())).thenReturn(Optional.of(user));
        when(jwtTokenService.generateToken(any(User.class), any())).thenReturn("mocked-jwt-token");

        LoginResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
        verify(userRepository, times(2)).save(any(User.class));
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    void register_ThrowsDataConflictException_WhenUsernameExists() {
        RegisterRequest request = new RegisterRequest("existinguser", "password", "test@example.com", "123", "Shop", "Address", "Type");
        
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> authenticationService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void logout_Success() {
        String token = "mocked-jwt-token";
        
        assertDoesNotThrow(() -> authenticationService.logout(token));
        verify(tokenBlacklistService).blacklist(token);
    }
}
