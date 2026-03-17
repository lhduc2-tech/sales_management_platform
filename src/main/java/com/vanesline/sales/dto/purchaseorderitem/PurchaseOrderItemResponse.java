package com.vanesline.sales.dto.purchaseorderitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemResponse {

    private UUID id;

    @JsonProperty("purchase_order_id")
    private UUID purchaseOrderId;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_name")
    private String productName;

    private Integer quantity;

    @JsonProperty("import_price")
    private BigDecimal importPrice;
}
