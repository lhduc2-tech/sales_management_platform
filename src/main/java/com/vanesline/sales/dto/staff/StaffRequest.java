package com.vanesline.sales.dto.staff;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;
}
