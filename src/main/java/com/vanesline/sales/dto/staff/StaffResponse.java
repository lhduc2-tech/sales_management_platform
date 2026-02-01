package com.vanesline.sales.dto.staff;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {

    private UUID id;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("shop_id")
    private UUID shopId;

    @JsonProperty("user_id")
    private UUID userId;

    private String status;

    private String position;

    // User info
    private String username;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("user_status")
    private String userStatus;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;
}
