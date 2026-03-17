package com.vanesline.sales.dto.stockmovement;

import com.vanesline.sales.dto.common.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchStockMovementRequest extends BaseRequest {

    private UUID productId;

    private UUID shopId;

    private String movementType;
}
