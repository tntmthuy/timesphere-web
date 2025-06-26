package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.request.ChangePasswordRequest;
import com.timesphere.timesphere.dto.response.ApiResponse;
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
    public List<UserSuggestionDto> searchInvitableUsers(
            @RequestParam String keyword,
            @RequestParam String teamId
    ) {
        return userService.searchUsersForInvitation(keyword, teamId);
    }
}