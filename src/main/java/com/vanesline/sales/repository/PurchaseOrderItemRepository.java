package com.vanesline.sales.repository;

import com.vanesline.sales.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderItemRepository
        extends JpaRepository<PurchaseOrderItem, UUID>,
        JpaSpecificationExecutor<PurchaseOrderItem> {
List<PurchaseOrderItem> findByPurchaseOrderId(UUID purchaseOrderId);
}
