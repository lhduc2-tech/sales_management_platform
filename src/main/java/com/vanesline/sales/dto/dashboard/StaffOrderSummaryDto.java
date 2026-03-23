package com.vanesline.sales.dto.dashboard;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffOrderSummaryDto {
    private Integer totalOrdersToday;
    private Integer pending;
    private Integer confirmed;
    private Integer cancelled;
    private BigDecimal averageOrderValue;
}
