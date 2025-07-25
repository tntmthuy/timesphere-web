package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dao.SearchRequest;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    // vào các chức năng cơ bản
    @GetMapping("/searchUser")
    @PreAuthorize("hasAuthority('admin:read_all_users')")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username) {

        SearchRequest request = new SearchRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setEmail(email);
        request.setUsername(username);

        List<User> users = userService.searchUsers(request);
        return ResponseEntity.ok(users);
    }
}
