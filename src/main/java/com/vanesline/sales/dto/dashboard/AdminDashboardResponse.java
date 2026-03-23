package com.vanesline.sales.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private RevenueSummaryDto revenue;
    private List<TopProductDto> topProducts;
    private InventorySummaryDto inventory;
}
