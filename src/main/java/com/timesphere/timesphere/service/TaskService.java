package com.timesphere.timesphere.service;


import com.timesphere.timesphere.dto.subtask.CreateSubtaskRequest;
import com.timesphere.timesphere.dto.task.CreateTaskRequest;
import com.timesphere.timesphere.dto.task.TaskResponseDTO;
import com.timesphere.timesphere.dto.task.UpdateTaskRequest;
import com.timesphere.timesphere.entity.*;
import com.timesphere.timesphere.entity.type.TeamRole;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.mapper.TaskMapper;
import com.timesphere.timesphere.repository.KanbanColumnRepository;
import com.timesphere.timesphere.repository.TaskRepository;
import com.timesphere.timesphere.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepo;
    private final KanbanColumnRepository columnRepo;
    private final TeamMemberRepository memberRepo;

    // 🛒TASK

    // gán task
    @Transactional
    public TaskResponseDTO assignMembers(String taskId, List<String> memberIds, User currentUser) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        TeamWorkspace team = task.getColumn().getTeam();

        boolean isOwner = memberRepo.existsByTeamAndUserAndTeamRole(team, currentUser, TeamRole.OWNER);
        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Chỉ OWNER được gán
        }

        List<TeamMember> membersToAssign = memberIds.stream()
                .map(id -> memberRepo.findById(Integer.parseInt(id))
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_TEAM)))
                .filter(m -> m.getTeam().getId().equals(team.getId()))
                .toList();

        task.getAssignees().clear();              // 👈 Fix lỗi ImmutableList
        task.getAssignees().addAll(membersToAssign);

        Task updated = taskRepo.save(task);
        return TaskMapper.toDto(updated);
    }


    @Transactional
    public Task createTask(CreateTaskRequest req, User currentUser) {
        KanbanColumn column = columnRepo.findById(req.getColumnId())
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        TeamWorkspace team = column.getTeam();

        TeamMember member = memberRepo.findByUserIdAndTeamId(currentUser.getId(), team.getId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        if (!TeamRole.OWNER.equals(member.getTeamRole())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        int nextPosition = taskRepo.findMaxPositionByColumnId(req.getColumnId()).orElse(-1) + 1;

        Task task = Task.builder()
                .taskTitle(req.getTitle())
                .description(req.getDescription())
                .position(nextPosition)
                .dateDue(req.getDateDue())
                .priority(req.getPriority())
                .column(column)
                .build();

        return taskRepo.save(task);
    }

    //cập nhật task
    @Transactional
    public TaskResponseDTO updateTask(String taskId, UpdateTaskRequest req, User requester) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        TeamWorkspace team = task.getColumn().getTeam();

        boolean isOwner = memberRepo.existsByTeamAndUserAndTeamRole(team, requester, TeamRole.OWNER);
        boolean isAssignee = task.getAssignees().stream()
                .anyMatch(m -> m.getUser().getId().equals(requester.getId()));

        if (!isOwner && !isAssignee) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (req.getTitle() != null) task.setTaskTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getDateDue() != null) task.setDateDue(req.getDateDue());
        if (req.getPriority() != null) task.setPriority(req.getPriority());

        Task updated = taskRepo.save(task);
        return TaskMapper.toDto(updated);
    }

    // xoá task
    @Transactional
    public void deleteTask(String taskId, User requester) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        TeamWorkspace team = task.getColumn().getTeam();
        boolean isOwner = memberRepo.existsByTeamAndUserAndTeamRole(team, requester, TeamRole.OWNER);

        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        taskRepo.delete(task);
    }

    public TaskResponseDTO getTaskById(String taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));
        return TaskMapper.toDto(task); // đã map sẵn subTasks + progress
    }

    // dời task sang các cột
    @Transactional
    public TaskResponseDTO moveTaskToColumnAtPosition(String taskId, String targetColumnId, int targetPosition, User currentUser) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        KanbanColumn currentColumn = task.getColumn();
        TeamWorkspace team = currentColumn.getTeam();

        // ✅ Kiểm tra quyền thao tác
        boolean isOwner = memberRepo.existsByTeamAndUserAndTeamRole(team, currentUser, TeamRole.OWNER);
        boolean isAssigned = task.getAssignees().stream()
                .anyMatch(m -> m.getUser().getId().equals(currentUser.getId()));

        if (!isOwner && !isAssigned) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Không có quyền thao tác task
        }

        // ✅ Lấy column đích
        KanbanColumn targetColumn = columnRepo.findById(targetColumnId)
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        // ✅ Lấy danh sách task hiện tại trong column đích
        List<Task> tasks = taskRepo.findByColumnIdOrderByPosition(targetColumnId);

        // ✅ Xoá task khỏi danh sách nếu đã nằm trong column này (tránh lỗi insert)
        tasks.removeIf(t -> t.getId().equals(taskId));

        // ✅ Thêm vào vị trí mới
        if (targetPosition > tasks.size()) {
            targetPosition = tasks.size(); // giới hạn index
        }
        tasks.add(targetPosition, task);

        // ✅ Cập nhật lại vị trí cho từng task
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(i);
            tasks.get(i).setColumn(targetColumn);
        }

        taskRepo.saveAll(tasks);
        log.info("📦 {} chuyển task {} sang column {} tại vị trí {}", currentUser.getEmail(), taskId, targetColumnId, targetPosition);

        return TaskMapper.toDto(task);
    }

    // 📮SUBTASK
    // tạo subtask
    @Transactional
    public Task createSubtask(CreateSubtaskRequest req) {
        Task parent = taskRepo.findById(req.getParentTaskId())
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        int nextPosition = taskRepo.findMaxSubtaskPositionByParentId(req.getParentTaskId())
                .orElse(-1) + 1;
        Task sub = Task.builder()
                .taskTitle(req.getTitle())
                .isComplete(false)
                .subtaskPosition(nextPosition)
                .parentTask(parent)
                .build();

        return taskRepo.save(sub);
    }

    // đánh dấu subtask
    @Transactional
    public void toggleSubtaskStatus(String subtaskId) {
        Task sub = taskRepo.findById(subtaskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        if (sub.getParentTask() == null) {
            throw new AppException(ErrorCode.SUBTASK_INVALID_PARENT);
        }

        sub.setIsComplete(!Boolean.TRUE.equals(sub.getIsComplete()));
        taskRepo.save(sub);
    }

    // dời subtask
    @Transactional
    public void reorderSubtask(String parentId, String subtaskId, int targetPosition) {
        List<Task> subtasks = taskRepo.findByParentTaskIdOrderBySubtaskPosition(parentId);

        Task moving = subtasks.stream()
                .filter(s -> s.getId().equals(subtaskId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        subtasks.remove(moving);
        subtasks.add(targetPosition, moving);

        for (int i = 0; i < subtasks.size(); i++) {
            subtasks.get(i).setSubtaskPosition(i);
        }

        taskRepo.saveAll(subtasks);
    }

    // cập nhật nôi dung subtask
    @Transactional
    public void updateSubtaskTitle(String subtaskId, String newTitle) {
        Task sub = taskRepo.findById(subtaskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        if (sub.getParentTask() == null)
            throw new AppException(ErrorCode.SUBTASK_INVALID_PARENT);
        sub.setTaskTitle(newTitle);
        taskRepo.save(sub);
    }

    // xóa subtask
    @Transactional
    public void deleteSubtask(String subtaskId) {
        Task sub = taskRepo.findById(subtaskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        if (sub.getParentTask() == null)
            throw new AppException(ErrorCode.SUBTASK_INVALID_PARENT);

        // Bước 1: xoá trước
        taskRepo.delete(sub);

        // Bước 2: lấy danh sách subtask còn lại
        List<Task> remaining = taskRepo.findByParentTaskIdOrderBySubtaskPosition(sub.getParentTask().getId());

        // Bước 3: cập nhật lại subtaskPosition
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setSubtaskPosition(i);
        }

        taskRepo.saveAll(remaining);
    }
}
