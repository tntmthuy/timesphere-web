package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.response.ApiResponse;
import com.timesphere.timesphere.dto.team.MemberInvite;
import com.timesphere.timesphere.dto.team.TeamCreateRequest;
import com.timesphere.timesphere.dto.team.TeamResponse;
import com.timesphere.timesphere.dto.team.TeamUpdateRequest;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> createTeam(
            @Valid @RequestBody TeamCreateRequest request,
            @AuthenticationPrincipal User user) {
        TeamResponse response = teamService.createTeam(request, user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo nhóm thành công!", response));
    }

    @PutMapping("/{teamId}/name")
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> renameTeam(
            @PathVariable String teamId,
            @Valid @RequestBody TeamUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        TeamResponse updated = teamService.updateTeamName(teamId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Đổi tên nhóm thành công!", updated));
    }

    //mời thêm thành viên
    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamResponse> addMembers(
            @PathVariable String teamId,
            @Valid @RequestBody List<@Valid MemberInvite> invites,
            @AuthenticationPrincipal User currentUser
    ) {
        TeamResponse response = teamService.addMembersToTeam(teamId, invites, currentUser);
        return ResponseEntity.ok(response);
    }

    //lấy danh sách thành viên nhóm hiện tại
    @GetMapping("/{teamId}")
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> getTeamDetail(
            @PathVariable String teamId,
            @AuthenticationPrincipal User user
    ) {
        TeamResponse response = teamService.getTeamDetail(teamId, user);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin nhóm thành công!", response));
    }

    //lấy các nhóm đang tham gia
    @GetMapping
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> getAllTeams(
            @AuthenticationPrincipal User user
    ) {
        List<TeamResponse> teams = teamService.getAllTeamsOfUser(user);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhóm thành công!", teams));
    }

    //rời nhóm
    @DeleteMapping("/{teamId}/leave")
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> leaveTeam(
            @PathVariable String teamId,
            @AuthenticationPrincipal User user
    ) {
        teamService.leaveTeam(teamId, user);
        return ResponseEntity.ok(ApiResponse.success("Đã rời khỏi nhóm!"));
    }

    //kick khỏi nhóm
    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> removeMember(
            @PathVariable String teamId,
            @PathVariable String userId,
            @AuthenticationPrincipal User currentUser
    ) {
        teamService.removeMember(teamId, userId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Đã xoá thành viên khỏi nhóm!"));
    }

    //xóa nhóm
    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasAuthority('user:manage_team')")
    public ResponseEntity<?> deleteTeam(
            @PathVariable String teamId,
            @AuthenticationPrincipal User user
    ) {
        teamService.deleteTeam(teamId, user);
        return ResponseEntity.ok(ApiResponse.success("Đã xoá nhóm thành công!"));
    }
}