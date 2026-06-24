package com.gongyi.platform.dto.volunteer;

import com.gongyi.platform.domain.enums.VolunteerApplicationStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.time.LocalDateTime;

public record VolunteerApplicationResponse(
        Long id,
        Long userId,
        String realName,
        String contactPhone,
        String skills,
        String experience,
        String availableTime,
        String motivation,
        VolunteerApplicationStatus status,
        String reviewRemark,
        LocalDateTime submitTime,
        LocalDateTime reviewTime,
        UserResponse user,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
