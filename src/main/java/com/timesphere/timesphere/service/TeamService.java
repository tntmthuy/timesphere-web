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

import java.time.LocalDateTime;
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

        // Người tạo là OWNER
        members.add(TeamMember.builder()
                .user(currentUser)
                .team(team)
                .teamRole(TeamRole.OWNER)
//                .joinedAt(LocalDateTime.now()) // 👈 thêm dòng này
                .build());


        // Mời thành viên
        if (request.getInvites() != null) {
            for (MemberInvite invite : request.getInvites()) {
                String email = invite.getEmail();
                if (email == null || email.equalsIgnoreCase(currentUser.getEmail())) continue;

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                TeamRole role = invite.getRole() == TeamRole.OWNER ? TeamRole.MEMBER : invite.getRole(); // Bảo vệ OWNER

                members.add(TeamMember.builder()
                        .user(user)
                        .team(team)
                        .teamRole(role != null ? role : TeamRole.MEMBER)
                        .build());
            }
        }

        teamMemberRepository.saveAll(members);
        return TeamMapper.toDto(team, members);
    }

    //đổi tên team
    public TeamResponse updateTeamName(String teamId, TeamUpdateRequest request, User currentUser) {
        TeamWorkspace team = findTeamOrThrow(teamId);

        verifyIsOwner(team, currentUser);

        team.setTeamName(request.getNewName());
        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }
        teamRepository.save(team);

        return TeamMapper.toDto(team, getMembers(team));
    }

    //lấy tất cả thành viên team hiện tại
    public List<TeamResponse> getAllTeamsOfUser(User user) {
        List<TeamMember> memberships = teamMemberRepository.findAllByUser(user);
        List<TeamResponse> result = new ArrayList<>();
        for (TeamMember m : memberships) {
            TeamWorkspace team = m.getTeam();
            List<TeamMember> members = teamMemberRepository.findAllByTeam(team);
            result.add(TeamMapper.toDto(team, members));
        }
        return result;
    }

    //lấy thông tin team
    public TeamResponse getTeamDetail(String teamId, User currentUser) {
        TeamWorkspace team = findTeamOrThrow(teamId);
        verifyIsMember(team, currentUser);
        return TeamMapper.toDto(team, getMembers(team));
    }

    //rời nhóm
    public void leaveTeam(String teamId, User currentUser) {
        TeamWorkspace team = findTeamOrThrow(teamId);
        TeamMember member = teamMemberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_JOINED_ANY_TEAM));

        if (member.getTeamRole() == TeamRole.OWNER) {
            throw new AppException(ErrorCode.OWNER_CANNOT_LEAVE);
        }

        teamMemberRepository.delete(member);
    }

    //kick khỏi nhóm
    public void removeMember(String teamId, String userId, User currentUser) {
        TeamWorkspace team = findTeamOrThrow(teamId);
        verifyIsOwner(team, currentUser);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.CANNOT_KICK_SELF);
        }

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_TEAM));

        teamMemberRepository.delete(member);
    }

    //xóa nhóm
    public void deleteTeam(String teamId, User currentUser) {
        TeamWorkspace team = findTeamOrThrow(teamId);
        verifyIsOwner(team, currentUser);

        // Xoá hết thành viên trước
        teamMemberRepository.deleteAllByTeam(team);
        teamRepository.delete(team);
    }

    // Helpers

    private TeamWorkspace findTeamOrThrow(String teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));
    }

    private void verifyIsOwner(TeamWorkspace team, User user) {
        boolean isOwner = teamMemberRepository.existsByTeamAndUserAndTeamRole(team, user, TeamRole.OWNER);
        if (!isOwner) throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    private void verifyIsMember(TeamWorkspace team, User user) {
        boolean isMember = teamMemberRepository.existsByTeamAndUser(team, user);
        if (!isMember) throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    private List<TeamMember> getMembers(TeamWorkspace team) {
        return teamMemberRepository.findAllByTeam(team);
    }

    //mời vào nhóm có sẵn
    public TeamResponse addMembersToTeam(String teamId, List<MemberInvite> invites, User currentUser) {
        TeamWorkspace team = findTeamOrThrow(teamId);
        verifyIsOwner(team, currentUser);

        List<TeamMember> newMembers = new ArrayList<>();

        for (MemberInvite invite : invites) {
            String email = invite.getEmail();
            TeamRole role = invite.getRole();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Không cho mời lại người đã trong nhóm
            boolean alreadyInTeam = teamMemberRepository.existsByTeamAndUser(team, user);
            if (alreadyInTeam) {
                throw new AppException(ErrorCode.USER_ALREADY_IN_TEAM);
            }

            newMembers.add(TeamMember.builder()
                    .user(user)
                    .team(team)
                    .teamRole(role == TeamRole.OWNER ? TeamRole.MEMBER : role)
                    .build());
        }

        teamMemberRepository.saveAll(newMembers);
        List<TeamMember> allMembers = teamMemberRepository.findAllByTeam(team);
        return TeamMapper.toDto(team, allMembers);
    }
}