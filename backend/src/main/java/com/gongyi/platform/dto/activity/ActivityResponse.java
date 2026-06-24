package com.gongyi.platform.dto.activity;

import com.gongyi.platform.domain.enums.ActivityStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        String title,
        String coverImage,
        String contentHtml,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime deadlineTime,
        ActivityStatus status,
        UserResponse creator,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
