package com.timesphere.timesphere.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotNull(message = "Content must not be null")
    @NotEmpty(message = "Content must not be empty")
    private String content;

    @NotNull(message = "User ID must not be null")
    private String userId; // Chỉ cần ID thay vì nguyên cả object
}
