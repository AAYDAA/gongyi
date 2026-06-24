package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.VolunteerApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VolunteerApplication {
    private Long id;
    private Long userId;
    private User user;
    private String realName;
    private String contactPhone;
    private String skills;
    private String experience;
    private String availableTime;
    private String motivation;
    private VolunteerApplicationStatus status;
    private String reviewRemark;
    private LocalDateTime submitTime;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
