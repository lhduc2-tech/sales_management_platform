package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.purchaseorder.*;
import com.vanesline.sales.dto.purchaseorderitem.*;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PurchaseOrderService {

    UUID create(PurchaseOrderRequest request);

    void addItem(UUID purchaseOrderId, PurchaseOrderItemRequest request);

    void confirm(UUID purchaseOrderId);

    PurchaseOrderResponse getById(UUID id);

    PageResponse<PurchaseOrderResponse> search(SearchPurchaseOrderRequest request, Pageable pageable);
}