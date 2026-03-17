package com.vanesline.sales.dto.purchaseorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vanesline.sales.dto.purchaseorderitem.PurchaseOrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {

    private UUID id;

    @JsonProperty("supplier_id")
    private UUID supplierId;

    private String status;

    private String note;

    @JsonProperty("purchase_order_items")
    private List<PurchaseOrderItemResponse> purchaseOrderItems;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("updated_by")
    private String updatedBy;
}
