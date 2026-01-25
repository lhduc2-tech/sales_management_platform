package com.vanesline.sales.dto.product;

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
public class SearchProductRequest extends BaseRequest {

    private String code;

    private String name;

    private UUID shopId;
}
