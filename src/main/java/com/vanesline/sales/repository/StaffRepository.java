package com.vanesline.sales.repository;

import com.vanesline.sales.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID>, JpaSpecificationExecutor<Staff> {

    Optional<Staff> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByUserIdAndStatus(UUID userId, String status);

    Optional<Staff> findByUserIdAndStatus(UUID userId, String status);

    @Query("SELECT s FROM Staff s LEFT JOIN FETCH s.user WHERE s.id = :id")
    Optional<Staff> findByIdWithUser(@Param("id") UUID id);

    @Query("SELECT s FROM Staff s LEFT JOIN FETCH s.user WHERE s.userId = :userId")
    Optional<Staff> findByUserIdWithUser(@Param("userId") UUID userId);

    @Query("SELECT s FROM Staff s LEFT JOIN FETCH s.user WHERE s.userId = :userId AND s.status = :status")
    Optional<Staff> findByUserIdAndStatusWithUser(@Param("userId") UUID userId, @Param("status") String status);
}
