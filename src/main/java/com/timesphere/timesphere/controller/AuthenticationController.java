package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.request.AuthenticationRequest;
import com.timesphere.timesphere.dto.request.ChangePasswordRequest;
import com.timesphere.timesphere.dto.response.AuthenticationResponse;
import com.timesphere.timesphere.dto.request.RegisterRequest;
import com.timesphere.timesphere.service.AuthenticationService;
import com.timesphere.timesphere.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserService userService;

    //đăng ký
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        System.out.println("🔍 Đăng ký email: " + request.getEmail());
        return ResponseEntity.ok(service.register(request));
    }

    //đăng nhập
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }



}
