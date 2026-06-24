package com.gongyi.platform.dto.activity;

import com.gongyi.platform.domain.enums.ActivityApplicationStatus;

import java.time.LocalDateTime;

public record MyApplicationResponse(
        Long id,
        Long activityId,
        String activityTitle,
        String activityLocation,
        LocalDateTime activityStartTime,
        LocalDateTime activityEndTime,
        LocalDateTime applyTime,
        ActivityApplicationStatus status,
        String remark
) {
}
