package com.gongyi.platform.dto.activity;

import com.gongyi.platform.domain.enums.ActivityApplicationStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long activityId,
        UserResponse volunteer,
        LocalDateTime applyTime,
        ActivityApplicationStatus status,
        String remark
) {
}
