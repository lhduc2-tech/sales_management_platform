package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.permission.PermissionRequest;
import com.vanesline.sales.dto.permission.PermissionResponse;
import com.vanesline.sales.entity.Permission;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.PermissionRepository;
import com.vanesline.sales.service.contract.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PermissionResponse create(PermissionRequest request) {
        if (permissionRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Permission with code '" + request.getCode() + "' already exists");
        }

        Permission permission = Permission.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .build();

        Permission savedPermission = permissionRepository.save(permission);
        return toResponse(savedPermission);
    }

    @Override
    public PermissionResponse update(UUID id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        // Check if code is being changed and if new code already exists
        if (!permission.getCode().equals(request.getCode()) && permissionRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Permission with code '" + request.getCode() + "' already exists");
        }

        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());

        Permission updatedPermission = permissionRepository.save(permission);
        return toResponse(updatedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getById(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));
        return toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (!permissionRepository.existsById(id)) {
            throw new EntityNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }

    private PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}
