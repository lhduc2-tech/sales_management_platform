package com.vanesline.sales.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequest {

    @NotBlank(message = "Permission code is required")
    @Size(max = 100, message = "Permission code must be at most 100 characters")
    private String code;

    private String description;
}
