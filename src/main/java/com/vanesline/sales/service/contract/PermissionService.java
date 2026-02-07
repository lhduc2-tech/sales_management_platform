package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.permission.PermissionRequest;
import com.vanesline.sales.dto.permission.PermissionResponse;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(UUID id, PermissionRequest request);

    PermissionResponse getById(UUID id);

    List<PermissionResponse> getAll();

    void delete(UUID id);
}
