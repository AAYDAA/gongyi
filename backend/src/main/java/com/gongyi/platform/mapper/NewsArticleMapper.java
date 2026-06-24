package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.NewsArticle;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NewsArticleMapper {

    @Select("SELECT * FROM news_articles WHERE id = #{id}")
    Optional<NewsArticle> findById(Long id);

    @Select("<script>" +
            "SELECT * FROM news_articles WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR summary LIKE CONCAT('%',#{keyword},'%') OR content_html LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY publish_time DESC, id DESC" +
            "</script>")
    List<NewsArticle> findByCondition(@Param("keyword") String keyword, @Param("status") String status);

    @Insert("INSERT INTO news_articles (author_id, title, cover_image, summary, content_html, status, publish_time, create_time, update_time) " +
            "VALUES (#{authorId}, #{title}, #{coverImage}, #{summary}, #{contentHtml}, #{status}, #{publishTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NewsArticle article);

    @Update("UPDATE news_articles SET title = #{title}, cover_image = #{coverImage}, summary = #{summary}, " +
            "content_html = #{contentHtml}, status = #{status}, publish_time = #{publishTime}, update_time = #{updateTime} WHERE id = #{id}")
    int update(NewsArticle article);

    @Delete("DELETE FROM news_articles WHERE id = #{id}")
    int deleteById(Long id);
}
