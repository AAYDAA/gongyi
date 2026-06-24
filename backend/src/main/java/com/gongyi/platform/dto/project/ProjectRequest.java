package com.gongyi.platform.dto.project;

import com.gongyi.platform.domain.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProjectRequest(
        String title,
        String coverImage,
        String summary,
        String contentHtml,
        BigDecimal goalAmount,
        ProjectStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
