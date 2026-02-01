package com.vanesline.sales.dto.shop;

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
public class ShopResponse {

    private UUID id;

    @JsonProperty("owner_shop_id")
    private UUID ownerShopId;

    @JsonProperty("shop_name")
    private String shopName;

    private String status;

    private String address;

    @JsonProperty("shop_type")
    private String shopType;

    @JsonProperty("shop_code")
    private String shopCode;

    @JsonProperty("opened_at")
    private OffsetDateTime openedAt;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("updated_by")
    private String updatedBy;
}
