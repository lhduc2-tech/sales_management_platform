package com.vanesline.sales.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSearchCriteria {

    private String supplierName;

    private String code;

    private String phoneNumber;

    private String email;

    private String taxCode;

    private String website;
}
