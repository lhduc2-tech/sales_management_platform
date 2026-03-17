package com.vanesline.sales.repository;

import com.vanesline.sales.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
@Repository
public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, UUID>,
        JpaSpecificationExecutor<PurchaseOrder> {
List<PurchaseOrder> findBySupplierId(UUID supplierId);
}
