package com.vanesline.sales.dto.role;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignPermissionsRequest {

    @NotEmpty(message = "Permission IDs list cannot be empty")
    private List<UUID> permissionIds;
}
