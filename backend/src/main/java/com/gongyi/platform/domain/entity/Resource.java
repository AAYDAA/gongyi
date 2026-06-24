package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.ResourceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Resource {
    private Long id;
    private String title;
    private String description;
    private String imageUrls;
    private ResourceStatus status;
    private Long publisherId;
    private User publisher;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
