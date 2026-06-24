package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.ActivityApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityApplication {
    private Long id;
    private Long activityId;
    private Activity activity;
    private Long volunteerId;
    private User volunteer;
    private LocalDateTime applyTime;
    private ActivityApplicationStatus status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
