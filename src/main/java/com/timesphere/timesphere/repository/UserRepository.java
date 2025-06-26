package com.timesphere.timesphere.repository;


import com.timesphere.timesphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("""
    SELECT u FROM User u
    WHERE 
        (LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND u.id NOT IN (
            SELECT m.user.id FROM TeamMember m WHERE m.team.id = :teamId
        )
        AND u.id NOT IN (
            SELECT i.invitedUser.id FROM TeamInvitation i
            WHERE i.team.id = :teamId AND i.status = 'PENDING'
        )
""")
    List<User> searchUsersInvitable(
            @Param("keyword") String keyword,
            @Param("teamId") String teamId
    );

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
