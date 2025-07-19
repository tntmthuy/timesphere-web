package com.timesphere.timesphere.service;

import com.timesphere.timesphere.entity.Task;
import com.timesphere.timesphere.entity.TeamMember;
import com.timesphere.timesphere.entity.type.NotificationType;
import com.timesphere.timesphere.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeadlineReminderService {

    private final TaskRepository taskRepo;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 1 * * *") // chạy mỗi ngày lúc 1h sáng
    public void remindTasksDueSoon() {
        LocalDateTime now = LocalDateTime.now();

        List<Task> tasks = taskRepo.findAll(); // hoặc chỉ chọn những task còn active

        for (Task task : tasks) {
            LocalDateTime deadline = task.getDateDue();
            long hoursUntilDue = Duration.between(now, deadline).toHours();

            String dateLabel = null;
            if (hoursUntilDue <= 24 && hoursUntilDue > 0) {
                dateLabel = "in less than 24 hours";
            } else if (hoursUntilDue <= 72 && hoursUntilDue > 24) {
                dateLabel = "in 3 days";
            } else if (deadline.isBefore(now)) {
                dateLabel = "was due";
            }

            if (dateLabel != null) {
                for (TeamMember member : task.getAssignees()) {
                    notificationService.notify(
                            member.getUser(),
                            null,
                            "Reminder: Task \"" + task.getTaskTitle() + "\" " + dateLabel,
                            "Deadline: " + deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            "/mainpage/task/" + task.getId(),
                            NotificationType.DEADLINE_REMINDER,
                            task.getId()
                    );
                }
            }
        }
    }
}