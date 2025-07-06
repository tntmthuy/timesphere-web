package com.timesphere.timesphere.mapper;

import com.timesphere.timesphere.dto.comment.AttachmentDTO;
import com.timesphere.timesphere.entity.Attachment;
import com.timesphere.timesphere.util.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {

    public AttachmentDTO toDto(Attachment attachment) {
        return AttachmentDTO.builder()
                .id(attachment.getId())
                .name(attachment.getName())
                .url(attachment.getUrl())
                .fileType(attachment.getFileType())
                .type(attachment.getType().name()) // IMAGE, PDF, VIDEO,...
                .size(attachment.getSize())
                .displaySize(FileUtils.formatSize(
                        attachment.getSize() != null ? attachment.getSize() : 0L))
                .build();
    }
}