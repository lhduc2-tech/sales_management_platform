package com.vanesline.sales.dto.order;


import com.vanesline.sales.dto.common.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class SearchOrderRequest extends BaseRequest {

    private String customerName;

    private String orderOrigin;

    private String deliveryService;

    private OffsetDateTime orderDate;

}
