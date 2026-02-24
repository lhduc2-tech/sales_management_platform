package com.vanesline.sales.dto.role;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private UUID id;

    private String code;

    private String name;

    private String description;

    private String status;

    private List<String> permissions;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
