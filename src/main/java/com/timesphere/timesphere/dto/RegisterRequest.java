package com.timesphere.timesphere.dto;

import com.timesphere.timesphere.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String first_name;
    private String last_name;

    private Role role;
}
