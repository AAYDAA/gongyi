package com.gongyi.platform.controller;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.common.Result;
import com.gongyi.platform.domain.enums.VolunteerApplicationStatus;
import com.gongyi.platform.dto.volunteer.ReviewVolunteerApplicationRequest;
import com.gongyi.platform.dto.volunteer.VolunteerApplicationRequest;
import com.gongyi.platform.dto.volunteer.VolunteerApplicationResponse;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.VolunteerApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/volunteer-applications")
public class VolunteerApplicationController {
    private final VolunteerApplicationService volunteerApplicationService;

    public VolunteerApplicationController(VolunteerApplicationService volunteerApplicationService) {
        this.volunteerApplicationService = volunteerApplicationService;
    }

    @PreAuthorize("hasAnyRole('USER','VOLUNTEER')")
    @GetMapping("/me")
    public Result<VolunteerApplicationResponse> mine() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(volunteerApplicationService.getMine(userId));
    }

    @PreAuthorize("hasAnyRole('USER','VOLUNTEER')")
    @PutMapping("/me")
    public Result<VolunteerApplicationResponse> upsertMine(@RequestBody VolunteerApplicationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(volunteerApplicationService.upsertMine(userId, request));
    }

    @PreAuthorize("hasAnyRole('USER','VOLUNTEER')")
    @PostMapping("/me/submit")
    public Result<VolunteerApplicationResponse> submitMine() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(volunteerApplicationService.submitMine(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Result<PageResult<VolunteerApplicationResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) VolunteerApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.ok(volunteerApplicationService.page(keyword, status, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/review")
    public Result<VolunteerApplicationResponse> review(@PathVariable Long id, @RequestBody ReviewVolunteerApplicationRequest request) {
        return Result.ok(volunteerApplicationService.review(id, request));
    }
}
