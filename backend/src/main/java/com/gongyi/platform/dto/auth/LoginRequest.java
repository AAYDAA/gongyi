package com.gongyi.platform.dto.auth;

public record LoginRequest(
        String account,
        String password
) {
}
