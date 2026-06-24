package com.gongyi.platform.controller;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.common.Result;
import com.gongyi.platform.domain.enums.OrganizationVerifyStatus;
import com.gongyi.platform.dto.org.OrganizationProfileRequest;
import com.gongyi.platform.dto.org.OrganizationProfileResponse;
import com.gongyi.platform.dto.org.VerifyOrganizationRequest;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.OrganizationService;
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
@RequestMapping("/api/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public Result<PageResult<OrganizationProfileResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrganizationVerifyStatus verifyStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        OrganizationVerifyStatus finalStatus = verifyStatus != null ? verifyStatus : OrganizationVerifyStatus.APPROVED;
        return Result.ok(organizationService.page(keyword, finalStatus, page, size));
    }

    @GetMapping("/{id}")
    public Result<OrganizationProfileResponse> detail(@PathVariable Long id) {
        return Result.ok(organizationService.getById(id));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/me")
    public Result<OrganizationProfileResponse> me() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(organizationService.getMyProfile(userId));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/me")
    public Result<OrganizationProfileResponse> updateMe(@RequestBody OrganizationProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(organizationService.upsertMyProfile(userId, request));
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/me/submit")
    public Result<OrganizationProfileResponse> submit() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(organizationService.submitVerification(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/verify")
    public Result<OrganizationProfileResponse> verify(@PathVariable Long id, @RequestBody VerifyOrganizationRequest request) {
        return Result.ok(organizationService.verify(id, request));
    }
}
