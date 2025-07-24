package com.timesphere.timesphere.dto.focus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFocusStats {
    private String name;
    private String avatar;
    private int totalMinutes;

    public UserFocusStats(String name, String avatar, int totalMinutes) {
        this.name = name;
        this.avatar = avatar;
        this.totalMinutes = totalMinutes;
    }
}