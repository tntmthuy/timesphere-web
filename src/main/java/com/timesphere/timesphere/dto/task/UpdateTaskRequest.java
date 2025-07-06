package com.timesphere.timesphere.dto.task;

import com.timesphere.timesphere.entity.type.Priority;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private LocalDateTime dateDue;
    private Priority priority;
}
