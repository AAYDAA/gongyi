package com.gongyi.platform.dto.resource;

import com.gongyi.platform.domain.enums.ResourceStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.time.LocalDateTime;

public record ResourceResponse(
        Long id,
        String title,
        String description,
        String imageUrls,
        ResourceStatus status,
        UserResponse publisher,
        LocalDateTime publishTime,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
