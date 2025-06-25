package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.response.ApiResponse;
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
}