package com.vanesline.sales.repository;

import com.vanesline.sales.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
@Repository
public interface StockMovementRepository
        extends JpaRepository<StockMovement, UUID>,
        JpaSpecificationExecutor<StockMovement> {
List<StockMovement> findByProductId(UUID productId);
List<StockMovement> findByShopId(UUID shopId);
}
