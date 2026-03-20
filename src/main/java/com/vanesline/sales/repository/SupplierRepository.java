package com.vanesline.sales.repository;

import com.vanesline.sales.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository 
    extends JpaRepository<Supplier, UUID>, JpaSpecificationExecutor<Supplier> {

    Optional<Supplier> findByEmailAndShopId(String email, UUID shopId);

    Optional<Supplier> findByCodeAndShopId(String code, UUID shopId);

    boolean existsByEmailAndShopId(String email, UUID shopId);

    boolean existsByCodeAndShopId(String code, UUID shopId);

    boolean existsByPhoneNumberAndShopId(String phoneNumber, UUID shopId);

    boolean existsByTaxCodeAndShopId(String taxCode, UUID shopId);
}
