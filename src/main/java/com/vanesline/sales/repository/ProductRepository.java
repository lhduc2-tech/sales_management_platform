package com.vanesline.sales.repository;

import com.vanesline.sales.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {

    Optional<Product> findByCode(String code);

    boolean existsByCode(String code);

    List<Product> findByShopIdOrderByUpdatedAtDesc(UUID shopId);

    @Query(value = "SELECT MAX(CAST(SUBSTRING(code, 3) AS INTEGER)) FROM products WHERE shop_id = :shopId AND code LIKE 'SP%'", nativeQuery = true)
    Optional<Integer> findMaxCodeNumberByShopId(@Param("shopId") UUID shopId);

    @Query(value = """
    SELECT
                p.id,
                p.name,
                c.category_name
            FROM products p
            LEFT JOIN categories c
                ON p.category_id = c.id
""", nativeQuery = true)
    public void findCategoryName(UUID categoryId);

    @Query("SELECT p FROM Product p JOIN Inventory i ON p.id = i.productId WHERE i.shopId = :shopId AND i.quantity < :quantity")
    List<Product> findProductsWithLowStock(@Param("shopId") UUID shopId, @Param("quantity") Integer quantity);

    @Query("SELECT p FROM Product p WHERE p.shopId = :shopId AND NOT EXISTS (" +
           "  SELECT 1 FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "  WHERE oi.productId = p.id AND o.paymentState = 'PAID' AND o.updatedAt >= :startDate AND o.updatedAt <= :endDate" +
           ")")
    Page<Product> findUnsoldProducts(
            @Param("shopId") java.util.UUID shopId,
            @Param("startDate") java.time.OffsetDateTime startDate,
            @Param("endDate") java.time.OffsetDateTime endDate,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.shopId = :shopId AND NOT EXISTS (" +
           "  SELECT 1 FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "  WHERE oi.productId = p.id AND o.paymentState = 'PAID'" +
           ")")
    Page<Product> findUnsoldProductsAllTime(
            @Param("shopId") java.util.UUID shopId,
            Pageable pageable);
}
