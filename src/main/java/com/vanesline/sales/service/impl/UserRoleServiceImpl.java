package com.vanesline.sales.service.impl;

import com.vanesline.sales.entity.Role;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.entity.UserRole;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.RoleRepository;
import com.vanesline.sales.repository.UserRepository;
import com.vanesline.sales.repository.UserRoleRepository;
import com.vanesline.sales.service.contract.UserRoleService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void assignRoles(UUID userId, List<UUID> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        String assignedBy = getCurrentUsername();

        for (UUID roleId : roleIds) {
            // Skip if already assigned
            if (userRoleRepository.existsByIdUserIdAndIdRoleId(userId, roleId)) {
                continue;
            }

            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

            UserRole userRole = UserRole.builder()
                    .id(new UserRole.UserRoleId(userId, roleId))
                    .user(user)
                    .role(role)
                    .assignedAt(OffsetDateTime.now())
                    .assignedBy(assignedBy)
                    .build();

            userRoleRepository.save(userRole);
        }
    }

    @Override
    public void removeRoles(UUID userId, List<UUID> roleIds) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        userRoleRepository.deleteByUserIdAndRoleIds(userId, roleIds);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
