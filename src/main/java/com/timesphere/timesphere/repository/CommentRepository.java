package com.timesphere.timesphere.repository;

import com.timesphere.timesphere.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
