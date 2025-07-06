package com.timesphere.timesphere.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentDTO {
    private String id;
    private String name;
    private String url;
    private String fileType;
    private String type;    // IMAGE hoặc FILE
    private Long size;      // Dung lượng tính theo byte
    private String displaySize; // VD: "2.3 MB"

}
