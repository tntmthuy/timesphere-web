package com.timesphere.timesphere.dto.kanban;

import com.timesphere.timesphere.entity.type.Priority;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TaskResponseDTO {
    private String id;
    private String taskTitle;
    private String description;
    private Priority priority;
    private Integer position;
    private LocalDateTime dateDue;
    private List<TaskAssigneeDTO> assignees;
    private List<SubtaskDTO> subTasks;

    private Double progress; // Tỷ lệ subtask hoàn thành (0.0 → 1.0)
}
