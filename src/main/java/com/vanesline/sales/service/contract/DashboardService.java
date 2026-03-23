package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.dashboard.AdminDashboardResponse;
import com.vanesline.sales.dto.dashboard.StaffDashboardResponse;

import java.util.UUID;

public interface DashboardService {
    AdminDashboardResponse getAdminDashboard(String username, String period);
    StaffDashboardResponse getStaffDashboard(String username);
}
