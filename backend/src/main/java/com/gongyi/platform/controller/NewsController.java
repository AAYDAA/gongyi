package com.gongyi.platform.controller;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.common.Result;
import com.gongyi.platform.domain.enums.NewsStatus;
import com.gongyi.platform.dto.news.NewsRequest;
import com.gongyi.platform.dto.news.NewsResponse;
import com.gongyi.platform.security.SecurityUtils;
import com.gongyi.platform.service.NewsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public Result<PageResult<NewsResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        NewsStatus finalStatus = status != null ? status : NewsStatus.PUBLISHED;
        return Result.ok(newsService.page(keyword, finalStatus, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-page")
    public Result<PageResult<NewsResponse>> adminPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.ok(newsService.page(keyword, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<NewsResponse> detail(@PathVariable Long id) {
        return Result.ok(newsService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result<NewsResponse> create(@RequestBody NewsRequest request) {
        Long authorId = SecurityUtils.getCurrentUserId();
        return Result.ok(newsService.create(request, authorId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Result<NewsResponse> update(@PathVariable Long id, @RequestBody NewsRequest request) {
        return Result.ok(newsService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return Result.ok(null);
    }
}
