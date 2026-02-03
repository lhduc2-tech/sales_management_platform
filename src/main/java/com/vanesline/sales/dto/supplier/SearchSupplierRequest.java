package com.vanesline.sales.dto.supplier;

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
public class SearchSupplierRequest extends BaseRequest {

    private String supplierName;

    private String code;

    private String phoneNumber;

    private String email;

    private String taxCode;

    private String website;
}
