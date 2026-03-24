package com.vanesline.sales.dto.inventory;

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
public class InventoryItemDto {

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("category_name")
    private String categoryName;

    private Integer quantity;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
}
