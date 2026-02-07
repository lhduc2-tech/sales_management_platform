package com.vanesline.sales.dto.orderitem;

import com.vanesline.sales.dto.common.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchOrderItemRequest extends BaseRequest {

    private UUID productId;

    private UUID orderId;

    private Integer minQuantity;

    private Integer maxQuantity;

    private BigDecimal minTotalPrice;

    private BigDecimal maxTotalPrice;

    private BigDecimal minUnitPrice;

    private BigDecimal maxUnitPrice;
}
