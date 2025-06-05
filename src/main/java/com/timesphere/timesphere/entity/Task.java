package com.timesphere.timesphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String parent_task_id;
    private String task_title;
    private String description;
    private String type;
    private LocalDateTime date_due;
    private Boolean task_is_complete;
    private Integer reminder_time;
    private String priority;

}
