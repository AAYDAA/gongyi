package com.gongyi.platform.dto.news;

import com.gongyi.platform.domain.enums.NewsStatus;

import java.time.LocalDateTime;

public record NewsRequest(
        String title,
        String coverImage,
        String summary,
        String contentHtml,
        NewsStatus status,
        LocalDateTime publishTime
) {
}
