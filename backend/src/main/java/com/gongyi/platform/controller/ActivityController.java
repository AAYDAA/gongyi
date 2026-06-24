package com.gongyi.platform.controller;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.common.Result;
import com.gongyi.platform.domain.enums.ActivityApplicationStatus;
import com.gongyi.platform.domain.enums.ActivityStatus;
import com.gongyi.platform.dto.activity.ActivityRequest;
import com.gongyi.platform.dto.activity.ActivityResponse;
import com.gongyi.platform.dto.activity.ApplyActivityRequest;
import com.gongyi.platform.dto.activity.ApplicationResponse;
import com.gongyi.platform.dto.activity.MyApplicationResponse;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.ActivityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public Result<PageResult<ActivityResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ActivityStatus status,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.ok(activityService.page(keyword, location, status, creatorId, page, size));
    }

    @GetMapping("/{id}")
    public Result<ActivityResponse> detail(@PathVariable Long id) {
        return Result.ok(activityService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @PostMapping
    public Result<ActivityResponse> create(@RequestBody ActivityRequest request) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        return Result.ok(activityService.create(request, creatorId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @PutMapping("/{id}")
    public Result<ActivityResponse> update(@PathVariable Long id, @RequestBody ActivityRequest request) {
        return Result.ok(activityService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Result.ok(null);
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/{id}/apply")
    public Result<ApplicationResponse> apply(@PathVariable Long id, @RequestBody(required = false) ApplyActivityRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String remark = request != null ? request.remark() : null;
        return Result.ok(activityService.applyActivity(id, userId, remark));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @GetMapping("/my")
    public Result<PageResult<ActivityResponse>> myPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        return Result.ok(activityService.getMyPublishedActivities(creatorId, page, size));
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @GetMapping("/applied/my")
    public Result<PageResult<MyApplicationResponse>> myApplied(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(activityService.myApplications(userId, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/review")
    public Result<ActivityResponse> review(@PathVariable Long id, @RequestParam ActivityStatus status) {
        return Result.ok(activityService.reviewActivity(id, status));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @GetMapping("/{id}/applications")
    public Result<PageResult<ApplicationResponse>> applications(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return Result.ok(activityService.pageApplications(id, operatorId, page, size));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @PutMapping("/applications/{id}/review")
    public Result<ApplicationResponse> reviewApplication(@PathVariable Long id, @RequestParam ActivityApplicationStatus status) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return Result.ok(activityService.reviewApplication(id, operatorId, status));
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PutMapping("/applications/{id}/cancel")
    public Result<ApplicationResponse> cancelMyApplication(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(activityService.cancelMyApplication(id, userId));
    }
}
