package com.timesphere.timesphere.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping
    @PreAuthorize("hasAuthority('admin:read')")
    public String get(){
        return "GET: admin controller";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    public String create(){
        return "POST: admin controller";
    }

    @PutMapping
    @PreAuthorize("hasAuthority('admin:update')")
    public String update(){
        return "UPDATE: admin controller";
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('admin:delete')")
    public String delete(){
        return "DELETE: admin controller";
    }
}
