package com.gongyi.platform.controller;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.common.Result;
import com.gongyi.platform.domain.enums.ResourceDonationStatus;
import com.gongyi.platform.domain.enums.ResourceStatus;
import com.gongyi.platform.dto.resource.ClaimResourceRequest;
import com.gongyi.platform.dto.resource.ResourceDonationResponse;
import com.gongyi.platform.dto.resource.ResourceRequest;
import com.gongyi.platform.dto.resource.ResourceResponse;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.ResourceService;
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
@RequestMapping("/api/resources")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public Result<PageResult<ResourceResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ResourceStatus status,
            @RequestParam(required = false) Long publisherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.ok(resourceService.page(keyword, status, publisherId, page, size));
    }

    @GetMapping("/{id}")
    public Result<ResourceResponse> detail(@PathVariable Long id) {
        return Result.ok(resourceService.getById(id));
    }

    @PreAuthorize("hasAnyRole('VOLUNTEER','ORGANIZATION','ADMIN')")
    @PostMapping
    public Result<ResourceResponse> create(@RequestBody ResourceRequest request) {
        Long publisherId = SecurityUtils.getCurrentUserId();
        return Result.ok(resourceService.create(request, publisherId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @PutMapping("/{id}")
    public Result<ResourceResponse> update(@PathVariable Long id, @RequestBody ResourceRequest request) {
        return Result.ok(resourceService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION','ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return Result.ok(null);
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/{id}/claim")
    public Result<ResourceDonationResponse> claim(@PathVariable Long id, @RequestBody(required = false) ClaimResourceRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer quantity = request != null ? request.quantity() : null;
        String remark = request != null ? request.remark() : null;
        return Result.ok(resourceService.claimResource(id, userId, quantity, remark));
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @GetMapping("/claims/my")
    public Result<PageResult<ResourceDonationResponse>> myClaims(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(resourceService.myClaims(userId, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/claims")
    public Result<PageResult<ResourceDonationResponse>> claims(
            @RequestParam(required = false) ResourceDonationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.ok(resourceService.pageClaims(status, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/claims/{id}/review")
    public Result<ResourceDonationResponse> reviewClaim(@PathVariable Long id, @RequestParam ResourceDonationStatus status) {
        return Result.ok(resourceService.reviewClaim(id, status));
    }
}
