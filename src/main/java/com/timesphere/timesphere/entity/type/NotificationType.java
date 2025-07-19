package com.timesphere.timesphere.entity.type;

public enum NotificationType {
    INVITE,
    JOIN_TEAM,
    TASK_ASSIGNED,         // 📌 Bạn được gán vào task
    COMMENT_PRIVATE,       // 🔒 Có bình luận riêng gửi cho bạn
    TASK_COMPLETED,        // ✅ Task bạn theo dõi đã hoàn thành
    DEADLINE_REMINDER      // ⏰ Task sắp đến hạn (tự động)
}
