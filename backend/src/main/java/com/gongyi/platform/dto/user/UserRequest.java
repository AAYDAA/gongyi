package com.gongyi.platform.dto.user;

import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.domain.enums.UserStatus;

public record UserRequest(
        String username,
        String phone,
        String email,
        String avatarUrl,
        UserRole role,
        UserStatus status
) {
}
