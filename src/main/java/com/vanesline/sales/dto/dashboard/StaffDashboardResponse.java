package com.vanesline.sales.dto.dashboard;

import java.util.List;
import com.vanesline.sales.dto.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDashboardResponse {
    private StaffOrderSummaryDto orderSummary;
    private List<ProductResponse> lowStockProducts;
}
