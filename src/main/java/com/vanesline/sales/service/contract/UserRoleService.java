package com.vanesline.sales.service.contract;

import java.util.List;
import java.util.UUID;

public interface UserRoleService {

    void assignRoles(UUID userId, List<UUID> roleIds);

    void removeRoles(UUID userId, List<UUID> roleIds);
}
