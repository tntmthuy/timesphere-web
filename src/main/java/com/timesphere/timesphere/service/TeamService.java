package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dto.team.MemberInvite;
import com.timesphere.timesphere.dto.team.TeamCreateRequest;
import com.timesphere.timesphere.dto.team.TeamResponse;
import com.timesphere.timesphere.dto.team.TeamUpdateRequest;
import com.timesphere.timesphere.entity.TeamMember;
import com.timesphere.timesphere.entity.TeamWorkspace;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.entity.type.TeamRole;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.mapper.TeamMapper;
import com.timesphere.timesphere.repository.TeamMemberRepository;
import com.timesphere.timesphere.repository.TeamRepository;
import com.timesphere.timesphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamResponse createTeam(TeamCreateRequest request, User currentUser) {
        if (request.getTeamName() == null || request.getTeamName().isBlank()) {
            throw new AppException(ErrorCode.TEAM_NAME_REQUIRED);
        }

        TeamWorkspace team = TeamWorkspace.builder()
                .teamName(request.getTeamName())
                .createdBy(currentUser)
                .description(request.getDescription())
                .build();
        teamRepository.save(team);

        List<TeamMember> members = new ArrayList<>();

        // Người tạo làm OWNER
        members.add(TeamMember.builder()
                .user(currentUser)
                .team(team)
                .teamRole(TeamRole.OWNER)
                .build());

        // Duyệt danh sách mời theo email
        if (request.getInvites() != null) {
            for (MemberInvite invite : request.getInvites()) {
                String inviteEmail = invite.getEmail();
                if (inviteEmail == null || inviteEmail.equals(currentUser.getEmail())) {
                    continue;
                }

                User user = userRepository.findByEmail(inviteEmail)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                members.add(TeamMember.builder()
                        .user(user)
                        .team(team)
                        .teamRole(invite.getRole() != null ? invite.getRole() : TeamRole.MEMBER)
                        .build());
            }
        }

        teamMemberRepository.saveAll(members);
        return TeamMapper.toDto(team, members);
    }

    //đổi tên nhóm
    public TeamResponse updateTeamName(String teamId, TeamUpdateRequest request, User currentUser) {
        TeamWorkspace team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));

        // Kiểm tra quyền OWNER
        boolean isOwner = teamMemberRepository.existsByTeamAndUserAndTeamRole(team, currentUser, TeamRole.OWNER);
        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        team.setTeamName(request.getNewName());
        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }
        teamRepository.save(team);

        List<TeamMember> members = teamMemberRepository.findAllByTeam(team);
        return TeamMapper.toDto(team, members);
    }
}
