package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.auth.ApiResponse;
import com.timesphere.timesphere.dto.focus.FocusSessionResponse;
import com.timesphere.timesphere.dto.focus.UserFocusStats;
import com.timesphere.timesphere.entity.FocusSession;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.service.FocusService;
import com.timesphere.timesphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/focus")
@RequiredArgsConstructor
public class FocusController {

    private final FocusService focusService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<FocusSessionResponse>> startSession(
            @AuthenticationPrincipal User currentUser,
            @RequestParam int targetMinutes,
            @RequestParam(required = false) String description // ✅ thêm dòng này
    ) {
        var response = focusService.startSession(currentUser, "focus", targetMinutes, description);
        return ResponseEntity.ok(ApiResponse.success("Started session", response));
    }

    @PostMapping("/end/{sessionId}")
    @PreAuthorize("hasAuthority('user:focus_sessions')")
    public ResponseEntity<ApiResponse<FocusSessionResponse>> endSession(
            @PathVariable Long sessionId,
            @RequestParam int actualMinutes
    ) {
        var response = focusService.endSession(sessionId, actualMinutes);
        return ResponseEntity.ok(ApiResponse.success("Session ended", response));
    }

    @GetMapping("/today")
    @PreAuthorize("hasAuthority('user:focus_sessions')")
    public ResponseEntity<ApiResponse<List<FocusSession>>> getTodaySessions(
            @AuthenticationPrincipal User currentUser
    ) {
        List<FocusSession> sessions = focusService.getTodaySessions(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Today's sessions", sessions));
    }

    @GetMapping("/stats/week")
    @PreAuthorize("hasAuthority('user:focus_sessions')")
    public ResponseEntity<ApiResponse<Integer>> getWeeklyFocusMinutes(
            @AuthenticationPrincipal User currentUser
    ) {
        int totalMinutes = focusService.getWeeklyFocusedMinutes(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Weekly focused minutes", totalMinutes));
    }

    // lấy toàn bộ phiên đã hoàn thành của 1 người
    @GetMapping("/completed")
    @PreAuthorize("hasAuthority('user:focus_sessions')")
    public ResponseEntity<ApiResponse<List<FocusSessionResponse>>> getCompletedSessions(
            @AuthenticationPrincipal User currentUser
    ) {
        List<FocusSessionResponse> responses = focusService.getCompletedSessions(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Các phiên đã hoàn thành", responses));
    }

    //xóa phiên
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('user:focus_sessions')")
    public ResponseEntity<ApiResponse<String>> deleteFocus(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User user
    ) {
        focusService.deleteSession(sessionId, user);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa phiên thành công"));
    }

    //thời gian tất cả
    @GetMapping("/stats/all")
    @PreAuthorize("hasAuthority('user:focus_sessions')")
    public ResponseEntity<ApiResponse<List<UserFocusStats>>> getAllFocusStats() {
        List<UserFocusStats> stats = focusService.getAllUserFocusStats();
        return ResponseEntity.ok(ApiResponse.success("Tổng thời gian tập trung của tất cả người dùng", stats));
    }
}