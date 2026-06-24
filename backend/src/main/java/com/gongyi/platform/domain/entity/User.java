package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.domain.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String avatarUrl;
    private UserRole role;
    private LocalDateTime registerTime;
    private UserStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
