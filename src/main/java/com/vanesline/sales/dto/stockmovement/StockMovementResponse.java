package com.vanesline.sales.dto.stockmovement;

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
public class StockMovementResponse {

    private UUID id;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("shop_id")
    private UUID shopId;

    @JsonProperty("movement_type")
    private String movementType;

    private Integer quantity;

    private String note;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("updated_by")
    private String updatedBy;
}
