package com.gongyi.platform.controller;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.common.Result;
import com.gongyi.platform.dto.donation.DonationResponse;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.DonationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public Result<PageResult<DonationResponse>> myDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long donorId = SecurityUtils.getCurrentUserId();
        return Result.ok(donationService.myDonations(donorId, page, size));
    }
}
