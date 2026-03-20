package com.vanesline.sales.dto.purchaseorder;

import com.vanesline.sales.dto.purchaseorderitem.PurchaseOrderItemRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRequest {

    @NotNull(message = "Supplier ID is required")
    private UUID supplierId;

    @Size(max = 255)
    private String note;
    private UUID shopId;
    private String createdBy;
    private String updatedBy;
}
