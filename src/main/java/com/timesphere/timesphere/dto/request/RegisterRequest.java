package com.timesphere.timesphere.dto;

import com.timesphere.timesphere.entity.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email không hợp lệ, hãy nhập đúng định dạng (ví dụ: example@gmail.com)"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$",
            message = "Password must contain at least one alphabetical character, one digit, one special character, and be at least 8 characters long.")
    private String password;

    private String firstname;
    private String lastname;

    private Role role;
}
