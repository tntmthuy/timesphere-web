package com.timesphere.timesphere.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping
    public String get(){
        return "GET: user controller";
    }

    @PostMapping
    public String create(){
        return "POST: user controller";
    }

    @PutMapping
    public String update(){
        return "UPDATE: user controller";
    }

    @DeleteMapping
    public String delete(){
        return "DELETE: user controller";
    }
}
