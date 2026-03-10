package com.vanesline.sales.repository;

import com.vanesline.sales.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {

    Optional<Product> findByCode(String code);

    boolean existsByCode(String code);

    List<Product> findByShopId(UUID shopId);

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
}
