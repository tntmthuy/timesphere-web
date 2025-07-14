package com.timesphere.timesphere.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.timesphere.timesphere.entity.type.Gender;
import com.timesphere.timesphere.entity.type.Role;
import com.timesphere.timesphere.entity.type.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 128, nullable = false, unique = true)
    private String email;

    @Column(length = 64, nullable = false)
    private String password;

    private String firstname;
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    private String avatarUrl;

    //xác thực 2 yếu tố
    private boolean mfaEnabled;
    private String secret;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

//    private LocalDateTime user_create_at;
//    private LocalDateTime user_update_at;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Token> tokens;

    @ManyToMany(mappedBy = "users")
    private List<FocusRoom> focusRooms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> members;

    @OneToMany(mappedBy = "user")
    private List<TodoList> todoLists;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;


    public String getFullName() {
        return firstname + " " + lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
