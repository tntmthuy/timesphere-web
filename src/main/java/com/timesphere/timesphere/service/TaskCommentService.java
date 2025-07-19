package com.timesphere.timesphere.service;

import com.timesphere.timesphere.Cloudinary.CloudinaryService;
import com.timesphere.timesphere.Cloudinary.CloudinaryUploadResult;
import com.timesphere.timesphere.dto.comment.CreateCommentRequest;
import com.timesphere.timesphere.dto.task.TaskCommentDTO;
import com.timesphere.timesphere.entity.Attachment;
import com.timesphere.timesphere.entity.Task;
import com.timesphere.timesphere.entity.TaskComment;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.entity.type.CommentVisibility;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.mapper.TaskCommentMapper;
import com.timesphere.timesphere.repository.*;
import com.timesphere.timesphere.util.FileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.timesphere.timesphere.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.timesphere.timesphere.exception.ErrorCode.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCommentService {

    private final TaskRepository taskRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final AuthenticationService authService;
    private final UserRepository userRepo;
    private final TaskCommentRepository taskCommentRepo;
    private final TaskCommentMapper commentMapper;
    private final CloudinaryService cloudinaryService;
    private final AttachmentRepository attachmentRepo;

    public String deleteComment(String commentId, User currentUser) {
        TaskComment comment = taskCommentRepo.findById(commentId)
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        if (!comment.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AppException(UNAUTHORIZED);
        }

        String teamId = comment.getTask().getColumn().getTeam().getId();

        List<Attachment> attachments = comment.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            for (Attachment file : attachments) {
                String cloudId = file.getCloudId();
                if (cloudId != null) {
                    try {
                        cloudinaryService.deleteFile(cloudId);
                    } catch (Exception ex) {
                        log.warn("❗Không thể xoá file trên Cloudinary: {}", cloudId, ex);
                    }
                }
            }
        }

        taskCommentRepo.delete(comment);
        return teamId;
    }

    @Transactional
    public TaskCommentDTO createComment(CreateCommentRequest req) {
        // 1. Lấy task cần bình luận
        Task task = taskRepo.findById(req.getTaskId())
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        // 2. Lấy user đang đăng nhập
        User user = authService.getCurrentUser();

        // 3. Kiểm tra quyền: người dùng phải thuộc team của column chứa task
        if (!teamMemberRepo.existsByTeamAndUser(task.getColumn().getTeam(), user)) {
            throw new AppException(UNAUTHORIZED);
        }

        // 4. Xử lý visible users nếu là comment PRIVATE
        CommentVisibility visibility = req.getVisibility() != null
                ? req.getVisibility()
                : CommentVisibility.PUBLIC;

        List<User> visibleUsers = List.of(); // default: trống
        if (CommentVisibility.PRIVATE.equals(visibility) && req.getVisibleToUserIds() != null) {
            visibleUsers = userRepo.findAllById(req.getVisibleToUserIds());
        }

        // 5. Tạo comment mới
        TaskComment comment = TaskComment.builder()
                .task(task)
                .createdBy(user)
                .content(req.getContent())
                .visibility(visibility)
                .visibleTo(visibleUsers)
                .build();

        // 6. Gắn file đính kèm nếu có
        if (req.getAttachments() != null && !req.getAttachments().isEmpty()) {
            List<Attachment> attachments = req.getAttachments().stream()
                    .map(dto -> Attachment.builder()
                            .name(dto.getName())
                            .url(dto.getUrl())
                            .fileType(dto.getFileType())
                            .size(dto.getSize())
                            .displaySize(dto.getDisplaySize())
                            .cloudId(cloudinaryService.extractCloudinaryId(dto.getUrl()))
                            .type(FileUtils.resolveAttachmentType(dto.getFileType()))
                            .comment(comment) // liên kết lại
                            .build())
                    .toList();

            comment.setAttachments(attachments);
        }

        // 7. Lưu và trả DTO
        TaskComment saved = taskCommentRepo.save(comment);
        return commentMapper.toDto(saved);
    }

    // Danh sách bình luận
    public List<TaskCommentDTO> getCommentsByTask(String taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        User currentUser = authService.getCurrentUser();

        if (!teamMemberRepo.existsByTeamAndUser(task.getColumn().getTeam(), currentUser)) {
            throw new AppException(UNAUTHORIZED);
        }

        List<TaskComment> allComments = taskCommentRepo.findAllByTaskOrderByCreatedAtDesc(task);

        List<TaskComment> filtered = allComments.stream()
                .filter(comment ->
                        // Nếu comment PUBLIC → mọi người đều thấy
                        CommentVisibility.PUBLIC.equals(comment.getVisibility())
                                // Nếu PRIVATE → chỉ hiện cho người tạo hoặc người được chỉ định
                                || comment.getCreatedBy().getId().equals(currentUser.getId())
                                || (comment.getVisibleTo() != null &&
                                comment.getVisibleTo().stream()
                                        .anyMatch(u -> u.getId().equals(currentUser.getId())))
                )
                .toList();

        return filtered.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public TaskCommentDTO updateComment(String commentId, User user, String content, List<MultipartFile> newFiles) {

        TaskComment comment = taskCommentRepo.findById(commentId)
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        if (!comment.getCreatedBy().getId().equals(user.getId())) {
            throw new AppException(UNAUTHORIZED);
        }

        // Cập nhật nội dung
        comment.setContent(content);

        // Xoá file cũ khỏi DB và Cloudinary
        for (Attachment oldFile : comment.getAttachments()) {
            try {
                cloudinaryService.deleteFile(oldFile.getCloudId());
            } catch (Exception ex) {
                log.warn("❌ Không xoá được file {}: {}", oldFile.getCloudId(), ex.getMessage());
            }
            attachmentRepo.delete(oldFile);
        }
        comment.getAttachments().clear();

        // Upload file mới
        for (MultipartFile file : newFiles) {
            try {
                CloudinaryUploadResult uploaded = cloudinaryService.uploadFile(file, "task-comments");
                String mimeType = uploaded.getResourceType() + "/" + uploaded.getFormat();

                Attachment attachment = Attachment.builder()
                        .url(uploaded.getUrl())
                        .cloudId(uploaded.getPublicId())
                        .fileType(mimeType)
                        .type(FileUtils.resolveAttachmentType(mimeType))
                        .name(file.getOriginalFilename())
                        .size(file.getSize())
                        .displaySize(FileUtils.formatSize(file.getSize()))
                        .comment(comment)
                        .build();

                attachmentRepo.save(attachment);
                comment.getAttachments().add(attachment);
            } catch (IOException ex) {
                log.warn("❌ Upload lỗi file {}: {}", file.getOriginalFilename(), ex.getMessage());
            }
        }

        taskCommentRepo.save(comment);
        log.info("✏️ {} sửa bình luận {}: {} file mới", user.getEmail(), commentId, newFiles.size());

        return commentMapper.toDto(comment);
    }

}
