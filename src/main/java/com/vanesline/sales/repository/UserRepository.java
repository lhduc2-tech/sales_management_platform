package com.vanesline.sales.repository;

import com.vanesline.sales.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

        @Query("SELECT DISTINCT u FROM User u " +
                        "LEFT JOIN FETCH u.userRoles ur " +
                        "LEFT JOIN FETCH ur.role r " +
                        "LEFT JOIN FETCH r.permissions " +
                        "WHERE u.username = :username")
        Optional<User> findByUsernameWithRolesAndPermissions(@Param("username") String username);

        @Query("SELECT DISTINCT u FROM User u " +
                        "LEFT JOIN FETCH u.userRoles ur " +
                        "LEFT JOIN FETCH ur.role")
        List<User> findAllWithRoles();

        @Query("SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.userRoles ur " +
                        "LEFT JOIN FETCH ur.role " +
                        "WHERE u.id = :id")
        Optional<User> findByIdWithRoles(@Param("id") UUID id);

        Optional<User> findByUsername(String username);

        Optional<User> findByEmail(String email);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

        boolean existsByPhoneNumber(String phoneNumber);

        Optional<User> findByGoogleId(String googleId);
}
