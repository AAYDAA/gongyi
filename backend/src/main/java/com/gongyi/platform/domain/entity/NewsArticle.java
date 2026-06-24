package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.NewsStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewsArticle {
    private Long id;
    private Long authorId;
    private User author;
    private String title;
    private String coverImage;
    private String summary;
    private String contentHtml;
    private NewsStatus status;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
