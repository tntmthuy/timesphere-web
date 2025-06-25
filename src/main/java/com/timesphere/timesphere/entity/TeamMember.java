package com.timesphere.timesphere.entity;

import com.timesphere.timesphere.entity.type.TeamRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "team_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "team_id"})
)
public class TeamMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private TeamWorkspace team;

    @Column(name = "team_role")
    @Enumerated(EnumType.STRING)
    private TeamRole teamRole; // OWNER, MEMBER...

//    private LocalDateTime joinedAt;
//
//    @PrePersist
//    protected void onJoin() {
//        this.joinedAt = LocalDateTime.now();
//    }
}
