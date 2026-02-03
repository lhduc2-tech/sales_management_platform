package com.vanesline.sales.repository;

import com.vanesline.sales.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID>, JpaSpecificationExecutor<Supplier> {

    Optional<Supplier> findByEmail(String email);

    Optional<Supplier> findByCode(String code);

    boolean existsByEmail(String email);

    boolean existsByCode(String code);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByTaxCode(String taxCode);
}
