package com.gongyi.platform.dto.resource;

import com.gongyi.platform.domain.enums.ResourceStatus;

public record ResourceRequest(
        String title,
        String description,
        String imageUrls,
        ResourceStatus status
) {
}
