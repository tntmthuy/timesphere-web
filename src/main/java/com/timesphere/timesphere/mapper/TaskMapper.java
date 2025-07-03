package com.timesphere.timesphere.mapper;

import com.timesphere.timesphere.dto.kanban.SubtaskDTO;
import com.timesphere.timesphere.dto.kanban.TaskResponseDTO;
import com.timesphere.timesphere.entity.Task;

import java.util.List;
import java.util.Optional;

public class TaskMapper {

    public static TaskResponseDTO toDto(Task task) {
        List<SubtaskDTO> subtaskDtos = Optional.ofNullable(task.getSubTasks())
                .orElse(List.of())
                .stream()
                .map(sub -> SubtaskDTO.builder()
                        .id(sub.getId())
                        .title(sub.getTaskTitle())
                        .isComplete(sub.getIsComplete())
                        .subtaskPosition(sub.getSubtaskPosition())
                        .build())
                .toList();

        double progress = 0;
        if (!subtaskDtos.isEmpty()) {
            long done = subtaskDtos.stream().filter(SubtaskDTO::getIsComplete).count();
            progress = (double) done / subtaskDtos.size();
        }

        return TaskResponseDTO.builder()
                .id(task.getId())
                .taskTitle(task.getTaskTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .position(task.getPosition())
                .dateDue(task.getDateDue())
                .subTasks(subtaskDtos)
                .progress(progress)
                .assignees(task.getAssignees().stream()
                        .map(TaskAssigneeMapper::toDto)
                        .toList())
                .build();
    }
}