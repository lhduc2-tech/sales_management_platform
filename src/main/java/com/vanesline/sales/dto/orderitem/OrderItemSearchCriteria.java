package com.vanesline.sales.dto.orderitem;

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
public class OrderItemSearchCriteria {

    private UUID productId;

    private UUID orderId;

    private Integer minQuantity;

    private Integer maxQuantity;

    private BigDecimal minTotalPrice;

    private BigDecimal maxTotalPrice;

    private BigDecimal minUnitPrice;

    private BigDecimal maxUnitPrice;
}
