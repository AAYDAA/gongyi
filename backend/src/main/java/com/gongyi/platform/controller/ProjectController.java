package com.gongyi.platform.controller;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.common.Result;
import com.gongyi.platform.domain.enums.ProjectStatus;
import com.gongyi.platform.dto.donation.DonationResponse;
import com.gongyi.platform.dto.project.DonateProjectRequest;
import com.gongyi.platform.dto.project.ProjectRequest;
import com.gongyi.platform.dto.project.ProjectResponse;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.DonationService;
import com.gongyi.platform.service.ProjectService;
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
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final DonationService donationService;

    public ProjectController(ProjectService projectService, DonationService donationService) {
        this.projectService = projectService;
        this.donationService = donationService;
    }

    @GetMapping
    public Result<PageResult<ProjectResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.ok(projectService.page(keyword, status, creatorId, page, size));
    }

    @GetMapping("/{id}")
    public Result<ProjectResponse> detail(@PathVariable Long id) {
        return Result.ok(projectService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @PostMapping
    public Result<ProjectResponse> create(@RequestBody ProjectRequest request) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        return Result.ok(projectService.create(request, creatorId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @PutMapping("/{id}")
    public Result<ProjectResponse> update(@PathVariable Long id, @RequestBody ProjectRequest request) {
        return Result.ok(projectService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.ok(null);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/donate")
    public Result<DonationResponse> donate(@PathVariable Long id, @RequestBody DonateProjectRequest request) {
        Long donorId = SecurityUtils.getCurrentUserId();
        return Result.ok(projectService.donate(id, donorId, request));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @GetMapping("/{id}/donations")
    public Result<PageResult<DonationResponse>> donations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.ok(donationService.projectDonations(id, page, size));
    }
}
