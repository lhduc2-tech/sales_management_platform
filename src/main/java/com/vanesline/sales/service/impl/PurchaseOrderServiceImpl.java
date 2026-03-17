package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.purchaseorder.*;
import com.vanesline.sales.dto.purchaseorderitem.*;
import com.vanesline.sales.entity.*;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.*;
import com.vanesline.sales.service.contract.InventoryService;
import com.vanesline.sales.service.contract.PurchaseOrderService;
import com.vanesline.sales.service.contract.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository poRepo;
    private final PurchaseOrderItemRepository itemRepo;
    private final InventoryService inventoryService;
    private final StockMovementService stockService;

    @Override
    public UUID create(PurchaseOrderRequest request) {

        PurchaseOrder po = PurchaseOrder.builder()
                .supplierId(request.getSupplierId())
                .note(request.getNote())
                .status("DRAFT")
                .build();

        po.setCreatedBy(request.getCreatedBy());

        poRepo.save(po);
        return po.getId();
    }

    @Override
    public void addItem(UUID purchaseOrderId, PurchaseOrderItemRequest request) {

        PurchaseOrder po = poRepo.findById(purchaseOrderId)
                .orElseThrow(() -> new EntityNotFoundException("PO not found"));

        if (!"DRAFT".equals(po.getStatus())) {
            throw new RuntimeException("Cannot add item");
        }

        PurchaseOrderItem item = PurchaseOrderItem.builder()
                .purchaseOrderId(purchaseOrderId)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .importPrice(request.getImportPrice())
                .build();

        itemRepo.save(item);
    }

    @Override
    public void confirm(UUID purchaseOrderId) {

        PurchaseOrder po = poRepo.findById(purchaseOrderId)
                .orElseThrow(() -> new EntityNotFoundException("PO not found"));

        if (!"DRAFT".equals(po.getStatus())) {
            throw new RuntimeException("Already confirmed");
        }

        List<PurchaseOrderItem> items = itemRepo.findByPurchaseOrderId(purchaseOrderId);

        for (PurchaseOrderItem item : items) {

            inventoryService.increaseStock(
                    item.getProductId(),
                    po.getShopId(),
                    item.getQuantity()
            );

            stockService.logImport(
                    item.getProductId(),
                    po.getShopId(),
                    item.getQuantity(),
                    "Import from PO"
            );
        }

        po.setStatus("CONFIRMED");
        poRepo.save(po);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getById(UUID id) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PurchaseOrderResponse> search(SearchPurchaseOrderRequest request, Pageable pageable) {
        Page<PurchaseOrder> page = poRepo.findAll(pageable);
        return PageResponse.from(page.map(po -> new PurchaseOrderResponse()));
    }
}