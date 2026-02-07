package com.vanesline.sales.dto.permission;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {

    private UUID id;
    private String code;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
