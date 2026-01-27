package com.vanesline.sales.dto.auth;

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
public class GoogleRegisterRequest {

    @NotBlank(message = "Google ID token is required")
    private String idToken;

    @NotBlank(message = "Shop name is required")
    @Size(max = 255, message = "Shop name must be at most 255 characters")
    private String shopName;

    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    @Size(max = 255, message = "Aim must be at most 255 characters")
    private String aim;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Phone number must be valid")
    private String phoneNumber;
}
