package com.gongyi.platform.dto.user;

import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.domain.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String phone,
        String email,
        String avatarUrl,
        UserRole role,
        LocalDateTime registerTime,
        UserStatus status
) {
}
