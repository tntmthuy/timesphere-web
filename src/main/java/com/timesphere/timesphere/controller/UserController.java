package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.CommentRequest;
import com.timesphere.timesphere.entity.Comment;
import com.timesphere.timesphere.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String get() {
        return "GET: user controller";
    }

    @PostMapping
    public String create() {
        return "POST: user controller";
    }

    @PutMapping
    public String update() {
        return "UPDATE: user controller";
    }

    @DeleteMapping
    public String delete() {
        return "DELETE: user controller";
    }

//chức năng cơ bản

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user_free:post_comment')")
    public ResponseEntity<String> postComment(@Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(userService.postComment(request));
    }


    //devteria

}
