package com.gongyi.platform.dto.auth;

public record RegisterRequest(
        String username,
        String password,
        String phone,
        String email
) {
}
