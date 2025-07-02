package com.timesphere.timesphere.entity;

import com.timesphere.timesphere.entity.type.AttachmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attachment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;           // Tên gốc của file
    private String url;            // Link CDN từ Cloudinary
    private String fileType;       // MIME Type (image/png, application/pdf, ...)
    private String cloudId;        // Public ID bên Cloudinary

    @Enumerated(EnumType.STRING)
    private AttachmentType type;   // IMAGE hoặc FILE

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskComment comment;   // Comment chứa file này
}
