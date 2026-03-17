package com.vanesline.sales.repository;

import com.vanesline.sales.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository
        extends JpaRepository<Inventory, UUID>,
        JpaSpecificationExecutor<Inventory> {
Optional<Inventory> findByProductIdAndShopId(UUID productId, UUID shopId);
}
