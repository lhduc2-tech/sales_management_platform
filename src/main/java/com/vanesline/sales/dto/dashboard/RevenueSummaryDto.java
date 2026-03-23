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
public class RevenueSummaryDto {
    private BigDecimal today;
    private BigDecimal thisWeek;
    private BigDecimal thisMonth;

    private Double todayGrowth;
    private Double weekGrowth;
    private Double monthGrowth;
}
