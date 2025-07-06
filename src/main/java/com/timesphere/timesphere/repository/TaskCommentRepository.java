package com.timesphere.timesphere.repository;

import com.timesphere.timesphere.entity.Task;
import com.timesphere.timesphere.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, String> {
    List<TaskComment> findAllByTask(Task task);
    void deleteByIdAndTask(String id, Task task);

    List<TaskComment> findAllByTaskOrderByCreatedAtDesc(Task task);
}
