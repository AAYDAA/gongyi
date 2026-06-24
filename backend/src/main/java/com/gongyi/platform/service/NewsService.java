package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.enums.NewsStatus;
import com.gongyi.platform.dto.news.NewsRequest;
import com.gongyi.platform.dto.news.NewsResponse;

public interface NewsService {
    NewsResponse create(NewsRequest request, Long authorId);

    NewsResponse update(Long id, NewsRequest request);

    void delete(Long id);

    NewsResponse getById(Long id);

    PageResult<NewsResponse> page(String keyword, NewsStatus status, int pageNum, int pageSize);
}
