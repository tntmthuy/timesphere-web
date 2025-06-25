package com.timesphere.timesphere.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.timesphere.timesphere.entity.type.Permission.*;

@RequiredArgsConstructor
public enum Role {
    FREE(
            Set.of(
                    FREE_READ,
                    FREE_CREATE,
                    FREE_DELETE,
                    FREE_UPDATE,

                    FREE_POST_COMMENT,
                    TEAM_WORKSPACE
            )
    ),
    PREMIUM(
            Set.of(
                    FREE_READ,
                    FREE_CREATE,
                    FREE_DELETE,
                    FREE_UPDATE,

                    PREMIUM_READ,
                    PREMIUM_CREATE,
                    PREMIUM_DELETE,
                    PREMIUM_UPDATE,

                    TEAM_WORKSPACE
            )
    ),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_CREATE,
                    ADMIN_DELETE,
                    ADMIN_UPDATE,
                    ADMIN_READ_ALL_USERS,

                    TEAM_WORKSPACE
            )
    );

    @Getter
    public final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
