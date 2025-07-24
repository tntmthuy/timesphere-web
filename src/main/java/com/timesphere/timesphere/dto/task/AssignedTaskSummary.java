package com.timesphere.timesphere.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignedTaskSummary {
    private Long taskId;
    private String title;
    private String teamId;
    private String teamName;
    private String teamUrl;

}
