package com.gongyi.platform.dto.project;

import com.gongyi.platform.domain.enums.ProjectStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String title,
        String coverImage,
        String summary,
        String contentHtml,
        BigDecimal goalAmount,
        BigDecimal raisedAmount,
        ProjectStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        UserResponse creator,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
