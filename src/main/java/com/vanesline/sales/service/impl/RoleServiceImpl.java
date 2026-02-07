package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.role.RoleRequest;
import com.vanesline.sales.dto.role.RoleResponse;
import com.vanesline.sales.dto.role.RoleSearchCriteria;
import com.vanesline.sales.entity.Permission;
import com.vanesline.sales.entity.Role;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.PermissionRepository;
import com.vanesline.sales.repository.RoleRepository;
import com.vanesline.sales.service.contract.RoleService;
import com.vanesline.sales.specification.RoleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Role with code '" + request.getCode() + "' already exists");
        }

        Role role = Role.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        Role savedRole = roleRepository.save(role);
        return toResponse(savedRole);
    }

    @Override
    public RoleResponse update(UUID id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        // Check if code is being changed and if new code already exists
        if (!role.getCode().equals(request.getCode()) && roleRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Role with code '" + request.getCode() + "' already exists");
        }

        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());

        Role updatedRole = roleRepository.save(role);
        return toResponse(updatedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(UUID id) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        return toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> search(RoleSearchCriteria criteria, Pageable pageable) {
        Page<Role> rolePage = roleRepository.findAll(RoleSpecification.withCriteria(criteria), pageable);

        List<RoleResponse> content = rolePage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<RoleResponse>builder()
                .content(content)
                .pageNumber(rolePage.getNumber())
                .pageSize(rolePage.getSize())
                .totalElements(rolePage.getTotalElements())
                .totalPages(rolePage.getTotalPages())
                .isFirst(rolePage.isFirst())
                .isLast(rolePage.isLast())
                .build();
    }

    @Override
    public void delete(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public void assignPermissions(UUID roleId, List<UUID> permissionIds) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        Set<Permission> permissions = permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Permission not found with id: " + permissionId)))
                .collect(Collectors.toSet());

        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
    }

    @Override
    public void removePermissions(UUID roleId, List<UUID> permissionIds) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        role.getPermissions().removeIf(permission -> permissionIds.contains(permission.getId()));
        roleRepository.save(role);
    }

    private RoleResponse toResponse(Role role) {
        List<String> permissionCodes = role.getPermissions() != null
                ? role.getPermissions().stream()
                        .map(Permission::getCode)
                        .collect(Collectors.toList())
                : List.of();

        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.getStatus())
                .permissions(permissionCodes)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
