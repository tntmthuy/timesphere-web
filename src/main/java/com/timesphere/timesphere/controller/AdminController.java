package com.timesphere.timesphere.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping
    public String get(){
        return "GET: admin controller";
    }

    @PostMapping
    public String create(){
        return "POST: admin controller";
    }

    @PutMapping
    public String update(){
        return "UPDATE: admin controller";
    }

    @DeleteMapping
    public String delete(){
        return "DELETE: admin controller";
    }
}
