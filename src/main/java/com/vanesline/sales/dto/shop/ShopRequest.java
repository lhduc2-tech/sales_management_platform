package com.vanesline.sales.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String aim;

    private String createdBy;

    private String updatedBy;
}
