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
public class InventorySummaryDto {
    private Integer totalStock;
    private List<ProductResponse> lowStockProducts;
    private List<TopProductDto> bestSellingProducts;
    private List<TopProductDto> slowSellingProducts;
}
