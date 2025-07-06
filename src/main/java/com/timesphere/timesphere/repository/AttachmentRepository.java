package com.timesphere.timesphere.repository;

import com.timesphere.timesphere.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {

    List<Attachment> findByComment_Id(String commentId);
    boolean existsByIdAndComment_CreatedBy_Id(String attachmentId, String userId);
    void deleteByComment_Id(String commentId);
}

