package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.NewsArticle;
import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.domain.enums.NewsStatus;
import com.gongyi.platform.dto.news.NewsRequest;
import com.gongyi.platform.dto.news.NewsResponse;
import com.gongyi.platform.mapper.NewsArticleMapper;
import com.gongyi.platform.service.NewsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private final NewsArticleMapper newsArticleMapper;
    private final UserServiceImpl userService;

    public NewsServiceImpl(NewsArticleMapper newsArticleMapper, UserServiceImpl userService) {
        this.newsArticleMapper = newsArticleMapper;
        this.userService = userService;
    }

    @Transactional
    @Override
    public NewsResponse create(NewsRequest request, Long authorId) {
        if (!StringUtils.hasText(request.title())) {
            throw new IllegalStateException("资讯标题不能为空");
        }
        User author = userService.getEntityById(authorId);
        LocalDateTime now = LocalDateTime.now();

        NewsArticle article = new NewsArticle();
        article.setAuthorId(authorId);
        article.setAuthor(author);
        article.setTitle(request.title());
        article.setCoverImage(request.coverImage());
        article.setSummary(request.summary());
        article.setContentHtml(request.contentHtml());
        article.setStatus(request.status() != null ? request.status() : NewsStatus.DRAFT);
        article.setPublishTime(request.publishTime());
        article.setCreateTime(now);
        article.setUpdateTime(now);
        applyPublishTime(article);
        newsArticleMapper.insert(article);
        return toResponse(article);
    }

    @Transactional
    @Override
    public NewsResponse update(Long id, NewsRequest request) {
        NewsArticle article = getEntity(id);
        if (request.title() != null) article.setTitle(request.title());
        if (request.coverImage() != null) article.setCoverImage(request.coverImage());
        if (request.summary() != null) article.setSummary(request.summary());
        if (request.contentHtml() != null) article.setContentHtml(request.contentHtml());
        if (request.status() != null) article.setStatus(request.status());
        if (request.publishTime() != null) article.setPublishTime(request.publishTime());
        article.setUpdateTime(LocalDateTime.now());
        applyPublishTime(article);
        newsArticleMapper.update(article);
        article.setAuthor(userService.getEntityById(article.getAuthorId()));
        return toResponse(article);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        newsArticleMapper.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public NewsResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<NewsResponse> page(String keyword, NewsStatus status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<NewsArticle> articles = newsArticleMapper.findByCondition(keyword,
                status != null ? status.name() : null);
        PageInfo<NewsArticle> pageInfo = new PageInfo<>(articles);
        List<NewsResponse> list = pageInfo.getList().stream().map(a -> {
            a.setAuthor(userService.getEntityById(a.getAuthorId()));
            return toResponse(a);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    private void applyPublishTime(NewsArticle article) {
        if (article.getStatus() == NewsStatus.PUBLISHED && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
    }

    private NewsArticle getEntity(Long id) {
        NewsArticle article = newsArticleMapper.findById(id).orElseThrow(() -> new IllegalStateException("资讯不存在"));
        article.setAuthor(userService.getEntityById(article.getAuthorId()));
        return article;
    }

    private NewsResponse toResponse(NewsArticle article) {
        return new NewsResponse(
                article.getId(),
                article.getTitle(),
                article.getCoverImage(),
                article.getSummary(),
                article.getContentHtml(),
                article.getStatus(),
                article.getPublishTime(),
                article.getAuthor() != null ? userService.toResponse(article.getAuthor()) : null,
                article.getCreateTime(),
                article.getUpdateTime()
        );
    }
}
