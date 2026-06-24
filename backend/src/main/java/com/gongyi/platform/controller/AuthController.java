package com.gongyi.platform.controller;

import com.gongyi.platform.common.Result;
import com.gongyi.platform.dto.auth.AuthResponse;
import com.gongyi.platform.dto.auth.LoginRequest;
import com.gongyi.platform.dto.auth.RegisterRequest;
import com.gongyi.platform.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<AuthResponse> register(@RequestBody RegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }
}
