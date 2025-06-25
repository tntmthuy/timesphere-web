package com.timesphere.timesphere.repository;

import com.timesphere.timesphere.entity.TeamMember;
import com.timesphere.timesphere.entity.TeamWorkspace;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.entity.type.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    boolean existsByTeamAndUserAndTeamRole(TeamWorkspace team, User user, TeamRole teamRole);
    List<TeamMember> findAllByTeam(TeamWorkspace team);
}
