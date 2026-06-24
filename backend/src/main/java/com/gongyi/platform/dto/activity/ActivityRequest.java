package com.gongyi.platform.dto.activity;

import com.gongyi.platform.domain.enums.ActivityStatus;

import java.time.LocalDateTime;

public record ActivityRequest(
        String title,
        String coverImage,
        String contentHtml,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime deadlineTime,
        ActivityStatus status
) {
}
