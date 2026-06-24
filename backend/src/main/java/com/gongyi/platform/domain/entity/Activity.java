package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.ActivityStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Activity {
    private Long id;
    private String title;
    private String coverImage;
    private String contentHtml;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime deadlineTime;
    private ActivityStatus status;
    private Long creatorId;
    private User creator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
