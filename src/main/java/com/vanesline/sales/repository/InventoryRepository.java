package com.vanesline.sales.repository;

import com.vanesline.sales.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.vanesline.sales.dto.inventory.InventoryItemDto;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository
        extends JpaRepository<Inventory, UUID>,
        JpaSpecificationExecutor<Inventory> {
    
    Optional<Inventory> findByProductIdAndShopId(UUID productId, UUID shopId);

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.shopId = :shopId")
    Long sumQuantityByShopId(@Param("shopId") UUID shopId);

    @Query("SELECT new com.vanesline.sales.dto.inventory.InventoryItemDto(p.id, p.code, p.name, c.categoryName, i.quantity, i.updatedAt) " +
           "FROM Inventory i " +
           "JOIN Product p ON i.productId = p.id " +
           "LEFT JOIN ProductCategory c ON p.categoryId = c.id " +
           "WHERE i.shopId = :shopId")
    Page<InventoryItemDto> getInventoryForShop(@Param("shopId") UUID shopId, Pageable pageable);
}
