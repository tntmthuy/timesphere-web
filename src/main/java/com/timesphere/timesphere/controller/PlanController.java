package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.plan.PlanInfo;
import com.timesphere.timesphere.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PlanController {

    private final PlanService planService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:payment_by_paypal')")
    public List<PlanInfo> getAllPlans() {
        return planService.getAvailablePlans();
    }
}