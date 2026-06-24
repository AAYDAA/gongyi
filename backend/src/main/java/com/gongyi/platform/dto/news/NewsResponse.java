package com.gongyi.platform.dto.news;

import com.gongyi.platform.domain.enums.NewsStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.time.LocalDateTime;

public record NewsResponse(
        Long id,
        String title,
        String coverImage,
        String summary,
        String contentHtml,
        NewsStatus status,
        LocalDateTime publishTime,
        UserResponse author,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
