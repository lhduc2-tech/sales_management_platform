package com.vanesline.sales.config;

import com.vanesline.sales.entity.*;
import com.vanesline.sales.repository.PermissionRepository;
import com.vanesline.sales.repository.RoleRepository;
import com.vanesline.sales.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initDemoData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Skip if data already exists
            if (userRepository.existsByUsername("admin")) {
                System.out.println("Demo data already exists, skipping initialization.");
                return;
            }

            System.out.println("Initializing demo data...");

            // Create Permissions
            Permission userRead = createPermission(permissionRepository, "user:read", "View user information");
            Permission userWrite = createPermission(permissionRepository, "user:write", "Create and update users");
            Permission userDelete = createPermission(permissionRepository, "user:delete", "Delete users");
            Permission roleRead = createPermission(permissionRepository, "role:read", "View roles");
            Permission roleWrite = createPermission(permissionRepository, "role:write", "Manage roles");
            Permission productRead = createPermission(permissionRepository, "product:read", "View products");
            Permission productWrite = createPermission(permissionRepository, "product:write", "Manage products");
            Permission orderRead = createPermission(permissionRepository, "order:read", "View orders");
            Permission orderWrite = createPermission(permissionRepository, "order:write", "Manage orders");

            // Create Roles
            Role adminRole = createRole(roleRepository, "ADMIN", "Administrator", "Full system access",
                    Set.of(userRead, userWrite, userDelete, roleRead, roleWrite, productRead, productWrite, orderRead,
                            orderWrite));

            Role managerRole = createRole(roleRepository, "MANAGER", "Manager", "Manage products and orders",
                    Set.of(userRead, productRead, productWrite, orderRead, orderWrite));

            Role salesRole = createRole(roleRepository, "SALES", "Sales Staff", "Handle orders",
                    Set.of(productRead, orderRead, orderWrite));

            Role viewerRole = createRole(roleRepository, "VIEWER", "Viewer", "Read-only access",
                    Set.of(userRead, productRead, orderRead));

            // Create Users - Password: password123
            String encodedPassword = passwordEncoder.encode("password123");

            createUser(userRepository, "admin", "admin@example.com", encodedPassword, true, adminRole);
            createUser(userRepository, "manager", "manager@example.com", encodedPassword, false, managerRole);
            createUser(userRepository, "sales01", "sales01@example.com", encodedPassword, false, salesRole);
            createUser(userRepository, "viewer", "viewer@example.com", encodedPassword, false, viewerRole);

            System.out.println("======================================");
            System.out.println("Demo data initialized successfully!");
            System.out.println("======================================");
            System.out.println("Test Accounts (password: password123):");
            System.out.println("  - admin   (ADMIN role)");
            System.out.println("  - manager (MANAGER role)");
            System.out.println("  - sales01 (SALES role)");
            System.out.println("  - viewer  (VIEWER role)");
            System.out.println("======================================");
        };
    }

    private Permission createPermission(PermissionRepository repo, String code, String description) {
        return repo.findByCode(code).orElseGet(() -> {
            Permission p = Permission.builder()
                    .code(code)
                    .description(description)
                    .build();
            return repo.save(p);
        });
    }

    private Role createRole(RoleRepository repo, String code, String name, String description,
            Set<Permission> permissions) {
        return repo.findByCode(code).orElseGet(() -> {
            Role r = Role.builder()
                    .code(code)
                    .name(name)
                    .description(description)
                    .status("ACTIVE")
                    .permissions(permissions)
                    .build();
            return repo.save(r);
        });
    }

    private void createUser(UserRepository repo, String username, String email,
            String passwordHash, boolean isSystem, Role role) {
        if (!repo.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .passwordHash(passwordHash)
                    .status("ACTIVE")
                    .isSystem(isSystem)
                    .build();

            // Save user first to get ID
            user = repo.save(user);

            // Now add role using the helper method
            UserRole userRole = UserRole.builder()
                    .id(new UserRole.UserRoleId(user.getId(), role.getId()))
                    .user(user)
                    .role(role)
                    .build();
            user.getUserRoles().add(userRole);

            repo.save(user);
        }
    }
}
