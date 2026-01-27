package com.vanesline.sales.repository;

import com.vanesline.sales.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository <ProductCategory, UUID>, JpaSpecificationExecutor<ProductCategory> {
}
