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
public class TopProductDto {
    private String productId;
    private String productName;
    private Integer sold;
    private BigDecimal revenue;
}
