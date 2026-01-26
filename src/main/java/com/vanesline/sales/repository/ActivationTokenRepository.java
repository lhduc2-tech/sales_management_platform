package com.vanesline.sales.repository;

import com.vanesline.sales.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {

    Optional<ActivationToken> findByTokenAndUsedFalse(String token);

    Optional<ActivationToken> findByUserIdAndUsedFalse(UUID userId);

    void deleteByUserId(UUID userId);
}
