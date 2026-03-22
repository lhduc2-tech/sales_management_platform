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
import com.vanesline.sales.specification.PurchaseOrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    @Override
    public UUID create(PurchaseOrderRequest request) {

        PurchaseOrder po = PurchaseOrder.builder()
                .supplierId(request.getSupplierId())
                .note(request.getNote())
                .status("DRAFT")
                .shopId(request.getShopId()) // ✅ THÊM
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
    public void updateItem(UUID itemId, UpdatePurchaseOrderItemRequest request) {

        PurchaseOrderItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        PurchaseOrder po = poRepo.findById(item.getPurchaseOrderId())
                .orElseThrow(() -> new EntityNotFoundException("PO not found"));

        if (!"DRAFT".equals(po.getStatus())) {
            throw new RuntimeException("Cannot update item after confirm");
        }

        item.setQuantity(request.getQuantity());
        item.setImportPrice(request.getImportPrice());

        itemRepo.save(item);
    }
    @Override
    public void deleteItem(UUID itemId) {

        PurchaseOrderItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        PurchaseOrder po = poRepo.findById(item.getPurchaseOrderId())
                .orElseThrow(() -> new EntityNotFoundException("PO not found"));

        if (!"DRAFT".equals(po.getStatus())) {
            throw new RuntimeException("Cannot delete item after confirm");
        }

        itemRepo.delete(item);
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

        PurchaseOrder po = poRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PO not found"));

        List<PurchaseOrderItem> items = itemRepo.findByPurchaseOrderId(id);

        return toResponse(po, items);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PurchaseOrderResponse> search(SearchPurchaseOrderRequest request, Pageable pageable) {

        Specification<PurchaseOrder> spec = PurchaseOrderSpecification.withCriteria(request);
        Page<PurchaseOrder> page = poRepo.findAll(spec, pageable);

        Page<PurchaseOrderResponse> responsePage = page.map(po -> {
            List<PurchaseOrderItem> items = itemRepo.findByPurchaseOrderId(po.getId());
            return toResponse(po, items);
        });

        return PageResponse.from(responsePage);
    }

    private PurchaseOrderResponse toResponse(PurchaseOrder po, List<PurchaseOrderItem> items) {

    String supplierName = supplierRepository.findById(po.getSupplierId())
            .map(Supplier::getSupplierName)
            .orElse("Unknown");

    List<PurchaseOrderItemResponse> itemResponses = items.stream()
            .map(i -> {

                String productName = productRepository.findById(i.getProductId())
                        .map(Product::getName)
                        .orElse("Unknown");

                return PurchaseOrderItemResponse.builder()
                        .id(i.getId())
                        .purchaseOrderId(i.getPurchaseOrderId())
                        .productId(i.getProductId())
                        .productName(productName)
                        .quantity(i.getQuantity())
                        .importPrice(i.getImportPrice())
                        .build();
            })
            .toList();

    return PurchaseOrderResponse.builder()
            .id(po.getId())
            .supplierId(po.getSupplierId())
            .supplierName(supplierName) // ✅ thêm
            .status(po.getStatus())
            .note(po.getNote())
            .purchaseOrderItems(itemResponses)
            .createdAt(po.getCreatedAt())
            .updatedAt(po.getUpdatedAt())
            .createdBy(po.getCreatedBy())
            .updatedBy(po.getUpdatedBy())
            .shopId(po.getShopId())
            .build();
}
}