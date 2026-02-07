package com.vanesline.sales.repository;

import com.vanesline.sales.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {

    List<UserRole> findByIdUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.id.userId = :userId AND ur.id.roleId IN :roleIds")
    void deleteByUserIdAndRoleIds(@Param("userId") UUID userId, @Param("roleIds") List<UUID> roleIds);

    boolean existsByIdUserIdAndIdRoleId(UUID userId, UUID roleId);
}
