package com.gongyi.platform.dto.project;

import com.gongyi.platform.domain.enums.DonationChannel;

import java.math.BigDecimal;

public record DonateProjectRequest(
        BigDecimal amount,
        DonationChannel channel,
        String remark
) {
}
