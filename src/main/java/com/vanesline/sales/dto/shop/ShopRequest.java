package com.vanesline.sales.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ShopRequest {

    @NotNull(message = "User info is required")
    private UUID ownerUserId;

    @NotBlank(message = "Shop name is required")
    private String shopName;

    @NotBlank(message = "Status is required")
    private String status;

    private String address;

    @Size(max = 30, message = "Shop type must be at most 30 characters")
    private String shopType;

    @Size(max = 50, message = "Shop code must be at most 50 characters")
    private String shopCode;

    private OffsetDateTime openedAt;

    private String createdBy;

    private String updatedBy;
}
