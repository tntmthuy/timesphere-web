package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dao.SearchRequest;
import com.timesphere.timesphere.dto.admin.*;
import com.timesphere.timesphere.dto.auth.ApiResponse;
import com.timesphere.timesphere.dto.plan.SubscriptionInfoDto;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.service.AdminService;
import com.timesphere.timesphere.service.UpgradeService;
import com.timesphere.timesphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final AdminService adminService;
    private final UpgradeService upgradeService;

    //tìm user
    @PostMapping("/users/search")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> searchUsers(@RequestBody SearchRequest request) {
        List<UserSummaryDto> result = adminService.searchUserSummaries(request);
        return ResponseEntity.ok(ApiResponse.success("Kết quả tìm kiếm", result));
    }

    //lấy danh sách người dùng
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getAllUsers() {
        List<UserSummaryDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Danh sách người dùng", users));
    }

    //set role
    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<String>> updateUserRole(
            @PathVariable String id,
            @RequestBody UpdateUserRoleRequest request
    ) {
        adminService.updateUserRole(id, request.role());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật vai trò thành công"));
    }

    //xoá
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        adminService.deleteUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công"));
    }

    //danh sách nhóm
    @GetMapping("/teams")
    public ResponseEntity<List<TeamDto>> getTeams() {
        return ResponseEntity.ok(adminService.getAllTeamsWithMembers());
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse>> getDashboardSummary() {
        SummaryResponse summary = adminService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Thống kê tổng quan", summary));
    }

    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<ChartPoint>>> getChartPoints(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        List<ChartPoint> result = adminService.getChartStats(range, fromDate, toDate, month, year);
        return ResponseEntity.ok(ApiResponse.success("Thống kê biểu đồ", result));
    }

    //danh sách tất cả giao dịch
    @GetMapping("/all-subscription")
    public ResponseEntity<ApiResponse<List<SubscriptionInfoDto>>> getAllSubscriptions() {
        List<SubscriptionInfoDto> result = upgradeService.getAllSubscriptions();
        return ResponseEntity.ok(ApiResponse.success("Danh sách tất cả giao dịch", result));
    }
}


