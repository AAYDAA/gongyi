package com.gongyi.platform.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof String s) {
            return Long.parseLong(s);
        }
        if (principal instanceof SecurityUser u) {
            return u.getUserId();
        }
        throw new IllegalStateException("未登录");
    }
}
