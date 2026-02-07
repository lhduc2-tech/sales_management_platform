package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.role.RoleRequest;
import com.vanesline.sales.dto.role.RoleResponse;
import com.vanesline.sales.dto.role.RoleSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleResponse create(RoleRequest request);

    RoleResponse update(UUID id, RoleRequest request);

    RoleResponse getById(UUID id);

    PageResponse<RoleResponse> search(RoleSearchCriteria criteria, Pageable pageable);

    void delete(UUID id);

    void assignPermissions(UUID roleId, List<UUID> permissionIds);

    void removePermissions(UUID roleId, List<UUID> permissionIds);
}
