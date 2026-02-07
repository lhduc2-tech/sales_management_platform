package com.vanesline.sales.dto.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRolesRequest {

    @NotEmpty(message = "Role IDs list cannot be empty")
    private List<UUID> roleIds;
}
