package com.vanesline.sales.dto.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {

    private UUID id;

    @JsonProperty("supplier_name")
    private String supplierName;

    private String code;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String email;

    private String website;

    @JsonProperty("tax_code")
    private String taxCode;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("shop_id")
    private UUID shopId;
}
