package com.vanesline.sales.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Shop name is required")
    @Size(max = 255, message = "Shop name must be at most 255 characters")
    private String shopName;

    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    @Size(max = 30, message = "Shop type must be at most 30 characters")
    private String shopType;
}
