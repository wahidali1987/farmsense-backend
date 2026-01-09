package com.farmsense.controller;

import com.farmsense.dto.DashboardResponse;
import com.farmsense.security.JwtUserPrincipal;
import com.farmsense.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse getDashboard(
            @AuthenticationPrincipal JwtUserPrincipal user
    ) {
        return dashboardService.getDashboard(user.getUserId());
    }
}

