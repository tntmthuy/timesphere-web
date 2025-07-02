package com.timesphere.timesphere.repository;

import com.timesphere.timesphere.entity.TeamWorkspace;
import com.timesphere.timesphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamWorkspace, String> {
    int countByCreatedBy(User user);
}
