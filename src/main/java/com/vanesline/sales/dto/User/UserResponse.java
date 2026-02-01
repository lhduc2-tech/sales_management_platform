package com.vanesline.sales.dto.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String status;

    @JsonProperty("is_system")
    private Boolean isSystem;

    @JsonProperty("shop_id")
    private UUID shopId;

    private List<String> roles;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("last_login_at")
    private LocalDateTime lastLoginAt;
}
