package com.gongyi.platform.controller;

import com.gongyi.platform.common.Result;
import com.gongyi.platform.dto.user.UserRequest;
import com.gongyi.platform.dto.user.UserResponse;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<UserResponse> me() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(userService.getById(userId));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public Result<UserResponse> updateMe(@RequestBody UserRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(userService.update(userId, request));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/apply-volunteer")
    public Result<UserResponse> applyVolunteer() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(userService.applyVolunteer(userId));
    }
}
