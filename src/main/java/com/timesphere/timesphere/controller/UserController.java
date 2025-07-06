package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.auth.ChangePasswordRequest;
import com.timesphere.timesphere.dto.auth.ApiResponse;
import com.timesphere.timesphere.service.TeamInvitationService;
import com.timesphere.timesphere.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TeamInvitationService invitationService;

    // Đổi mật khẩu khi đã đăng nhập
    @PatchMapping("/change-password")
    @PreAuthorize("")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal connectUser
    ) {
        userService.changePassword(request, connectUser);
        return ResponseEntity.ok(
                ApiResponse.success("Đổi mật khẩu thành công!")
        );
    }


    @GetMapping("/search-invitable")
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> searchInvitableUsers(
            @RequestParam String keyword,
            @RequestParam String teamId
    ) {
        var result = invitationService.searchUsersForInvitation(keyword, teamId);
        return ResponseEntity.ok(ApiResponse.success("Gợi ý thành viên khả dụng", result));
    }

    @GetMapping("/search-new-team")
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> suggestForNewTeam(@RequestParam(required = false) String keyword) {
        var suggestions = userService.searchUsersForNewTeam(keyword);
        return ResponseEntity.ok(ApiResponse.success("Gợi ý thành viên khả dụng", suggestions));
    }
}