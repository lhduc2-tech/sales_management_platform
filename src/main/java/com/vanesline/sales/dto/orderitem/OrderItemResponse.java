package com.vanesline.sales.dto.orderitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private UUID id;

    @JsonProperty("product_id")
    private UUID productId;

    private Integer quantity;

    @JsonProperty("order_id")
    private UUID orderId;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;
}
