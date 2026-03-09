package com.vanesline.sales.dto.order;

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
public class SearchOrderCriteria {

    private String customerName;

    private String orderOrigin;

    private String deliveryService;

    private OffsetDateTime orderDate;

    private UUID shopId;
}
