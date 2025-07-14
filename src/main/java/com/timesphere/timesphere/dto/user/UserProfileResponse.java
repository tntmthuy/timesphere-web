package com.timesphere.timesphere.dto.user;

import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.entity.type.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private Gender gender;
    private String avatarUrl;

    public static UserProfileResponse from(User u) {
        return new UserProfileResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstname(),
                u.getLastname(),
                u.getGender(),
                u.getAvatarUrl()
        );
    }
}
