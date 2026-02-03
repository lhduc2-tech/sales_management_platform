package com.vanesline.sales.dto.order;

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

// data return from an api
public class OrderResponse {
    private UUID id;

    @JsonProperty ("order_date")
    private OffsetDateTime orderDate;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("order_origin")
    private String orderOrigin;

    private BigDecimal price;

    @JsonProperty ("payment_state")
    private String paymentState;

    @JsonProperty ("process_state")
    private String processState;

    @JsonProperty("delivery_service")
    private String deliveryService;

    @JsonProperty("shop_id")
    private UUID shopId;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("updated_by")
    private String updatedBy;
}
