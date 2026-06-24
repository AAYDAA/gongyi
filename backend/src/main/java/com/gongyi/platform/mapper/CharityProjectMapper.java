package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.CharityProject;
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
public interface CharityProjectMapper {

    @Select("SELECT * FROM charity_projects WHERE id = #{id}")
    Optional<CharityProject> findById(Long id);

    @Select("<script>" +
            "SELECT * FROM charity_projects WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR summary LIKE CONCAT('%',#{keyword},'%') OR content_html LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='creatorId != null'> AND creator_id = #{creatorId}</if>" +
            " ORDER BY id DESC" +
            "</script>")
    List<CharityProject> findByCondition(@Param("keyword") String keyword, @Param("status") String status, @Param("creatorId") Long creatorId);

    @Insert("INSERT INTO charity_projects (creator_id, title, cover_image, summary, content_html, goal_amount, raised_amount, " +
            "status, start_time, end_time, create_time, update_time) VALUES (#{creatorId}, #{title}, #{coverImage}, #{summary}, " +
            "#{contentHtml}, #{goalAmount}, #{raisedAmount}, #{status}, #{startTime}, #{endTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CharityProject project);

    @Update("UPDATE charity_projects SET title = #{title}, cover_image = #{coverImage}, summary = #{summary}, " +
            "content_html = #{contentHtml}, goal_amount = #{goalAmount}, raised_amount = #{raisedAmount}, " +
            "status = #{status}, start_time = #{startTime}, end_time = #{endTime}, update_time = #{updateTime} WHERE id = #{id}")
    int update(CharityProject project);

    @Delete("DELETE FROM charity_projects WHERE id = #{id}")
    int deleteById(Long id);
}
