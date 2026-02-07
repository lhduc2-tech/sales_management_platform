package com.vanesline.sales.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {

    @NotBlank(message = "Role code is required")
    @Size(max = 50, message = "Role code must be at most 50 characters")
    private String code;

    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must be at most 100 characters")
    private String name;

    private String description;

    @Builder.Default
    private String status = "ACTIVE";
}
