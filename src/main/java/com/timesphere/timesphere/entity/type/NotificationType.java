package com.timesphere.timesphere.entity.type;

public enum NotificationType {
    TASK_ASSIGNED,         // Bạn được gán task mới 📌
    COMMENT_MENTIONED,     // Bạn bị mention trong comment 💬
    TASK_COMPLETED,        // Task bạn theo dõi đã hoàn thành ✅

    // Nhắc việc sắp đến hạn ⏰
    REMINDER_AUTO,        // hệ thống nhắc
    REMINDER_MANUAL,      // người dùng nhắc

    }
