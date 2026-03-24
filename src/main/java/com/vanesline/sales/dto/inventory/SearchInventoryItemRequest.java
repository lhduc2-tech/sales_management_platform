package com.vanesline.sales.dto.inventory;

import com.vanesline.sales.dto.common.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchInventoryItemRequest extends BaseRequest {

    private String searchQuery;

}
