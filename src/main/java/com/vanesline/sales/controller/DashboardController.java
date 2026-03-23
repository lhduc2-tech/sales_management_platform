package com.vanesline.sales.controller;

import com.vanesline.sales.dto.dashboard.AdminDashboardResponse;
import com.vanesline.sales.dto.dashboard.StaffDashboardResponse;
import com.vanesline.sales.service.contract.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("${application.path.base.private}/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard(
            @RequestParam(value = "period", defaultValue = "all") String period) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(dashboardService.getAdminDashboard(username, period));
    }

    @GetMapping("/staff")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<StaffDashboardResponse> getStaffDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(dashboardService.getStaffDashboard(username));
    }
}
