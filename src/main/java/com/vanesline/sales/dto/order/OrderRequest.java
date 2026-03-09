package com.vanesline.sales.dto.order;

import jakarta.validation.constraints.*;
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
// attributes needed to create order
public class OrderRequest {

    //date --> not Null
    @NotNull(message = "Order Date is required")
    private OffsetDateTime orderDate;

    private UUID customerId;

    @NotBlank(message = "Order origin is required")
    @Size(max = 255)
    private String orderOrigin;

    @Min(0)
    private BigDecimal price;

    private String paymentState;
    private String processState;
    @Size(max = 255)
    private String deliveryService;

    private UUID shopId;

    private String createdBy;
    private String updatedBy;
}
