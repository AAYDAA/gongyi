package com.gongyi.platform.dto.donation;

import com.gongyi.platform.domain.enums.DonationChannel;
import com.gongyi.platform.domain.enums.DonationStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DonationResponse(
        Long id,
        Long projectId,
        String projectTitle,
        UserResponse donor,
        BigDecimal amount,
        DonationChannel channel,
        DonationStatus status,
        LocalDateTime donateTime,
        String remark
) {
}
