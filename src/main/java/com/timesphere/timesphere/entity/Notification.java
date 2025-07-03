package com.timesphere.timesphere.entity;

import com.timesphere.timesphere.entity.type.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String message;         // Nội dung chính hiển thị
    private Boolean isRead;         // Đã đọc hay chưa
    private String targetUrl;       // FE biết chuyển hướng đến đâu

    @Enumerated(EnumType.STRING)
    private NotificationType type;  // Loại thông báo (TAGGED, ASSIGNED...)

    @ManyToOne(fetch = FetchType.LAZY)
    private User recipient;         // Người nhận thông báo

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;            // Người tạo ra sự kiện
}
