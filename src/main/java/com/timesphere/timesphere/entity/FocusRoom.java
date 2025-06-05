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
@Table(name = "focus_room")
public class FocusRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String room_name;
    private LocalDateTime room_create_at;

    @ManyToMany
    @JoinTable(
            name = "rooms_users",
            joinColumns = {
                    @JoinColumn(name = "user_id")
            },

            inverseJoinColumns = {
                    @JoinColumn(name = "room_id")
            }
    )
    private List<User> users;
}
