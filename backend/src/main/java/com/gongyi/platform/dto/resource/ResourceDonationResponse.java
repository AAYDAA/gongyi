package com.gongyi.platform.dto.resource;

import com.gongyi.platform.domain.enums.ResourceDonationStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.time.LocalDateTime;

public record ResourceDonationResponse(
        Long id,
        Long resourceId,
        String resourceTitle,
        UserResponse donor,
        Integer quantity,
        ResourceDonationStatus status,
        String remark,
        LocalDateTime donateTime
) {
}
