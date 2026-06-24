package com.gongyi.platform.dto.auth;

import com.gongyi.platform.dto.user.UserResponse;

public record AuthResponse(
        String token,
        UserResponse userInfo
) {
}
