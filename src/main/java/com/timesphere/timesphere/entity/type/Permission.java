package com.timesphere.timesphere.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    FREE_POST_COMMENT("user_free:post_comment"),

    ADMIN_READ_ALL_USERS("admin:read_all_users"),

    TEAM_WORKSPACE("user:manage_team"),

    KANBAN_BOARD("user:manage_board")

    ;

    private final String permission;
}
