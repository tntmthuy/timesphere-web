package com.timesphere.timesphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_workspace")
public class TeamWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String team_name;
    private LocalDateTime team_create_at;

    @ManyToMany
    @JoinTable(
            name = "teams_users",
            joinColumns = {
                    @JoinColumn(name = "team_id")
            },

            inverseJoinColumns = {
                    @JoinColumn(name = "user_id")
            }
    )
    private List<User> users;
}
