package com.timesphere.timesphere.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    FREE_READ("user_free::read"),
    FREE_CREATE("user_free::create"),
    FREE_DELETE("user_free::delete"),
    FREE_UPDATE("user_free::update"),

    PREMIUM_READ("user_premium: read"),
    PREMIUM_CREATE("user_premium::create"),
    PREMIUM_DELETE("user_premium::delete"),
    PREMIUM_UPDATE("user_premium::update"),

    ADMIN_READ("admin::read"),
    ADMIN_CREATE("admin::create"),
    ADMIN_DELETE("admin::delete"),
    ADMIN_UPDATE("admin::update"),

        ;

    @Getter
    private final String permission;
}
