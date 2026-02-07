package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.role.*;
import com.vanesline.sales.service.contract.RoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<RoleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RoleResponse> getById(@PathVariable UUID id) {
        RoleResponse response = roleService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<PageResponse<RoleResponse>> search(
            @RequestBody(required = false) SearchRoleRequest request) {
        if (request == null) {
            request = new SearchRoleRequest();
        }

        RoleSearchCriteria criteria = RoleSearchCriteria.builder()
                .code(request.getCode())
                .name(request.getName())
                .status(request.getStatus())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<RoleResponse> response = roleService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<Void> assignPermissions(
            @PathVariable UUID id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        roleService.assignPermissions(id, request.getPermissionIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:write')")
    public ResponseEntity<Void> removePermissions(
            @PathVariable UUID id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        roleService.removePermissions(id, request.getPermissionIds());
        return ResponseEntity.noContent().build();
    }
}
