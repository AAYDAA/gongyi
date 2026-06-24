package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CharityProject {
    private Long id;
    private Long creatorId;
    private User creator;
    private String title;
    private String coverImage;
    private String summary;
    private String contentHtml;
    private BigDecimal goalAmount;
    private BigDecimal raisedAmount;
    private ProjectStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
