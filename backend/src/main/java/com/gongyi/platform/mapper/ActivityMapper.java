package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.Activity;
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
public interface ActivityMapper {

    @Select("SELECT * FROM activities WHERE id = #{id}")
    Optional<Activity> findById(Long id);

    @Select("<script>" +
            "SELECT * FROM activities WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR content_html LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "<if test='location != null and location != \"\"'> AND location LIKE CONCAT('%',#{location},'%')</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='creatorId != null'> AND creator_id = #{creatorId}</if>" +
            " ORDER BY id DESC" +
            "</script>")
    List<Activity> findByCondition(@Param("keyword") String keyword, @Param("location") String location,
                                   @Param("status") String status, @Param("creatorId") Long creatorId);

    @Insert("INSERT INTO activities (title, cover_image, content_html, location, start_time, end_time, deadline_time, " +
            "status, creator_id, create_time, update_time) VALUES (#{title}, #{coverImage}, #{contentHtml}, #{location}, " +
            "#{startTime}, #{endTime}, #{deadlineTime}, #{status}, #{creatorId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Activity activity);

    @Update("UPDATE activities SET title = #{title}, cover_image = #{coverImage}, content_html = #{contentHtml}, " +
            "location = #{location}, start_time = #{startTime}, end_time = #{endTime}, deadline_time = #{deadlineTime}, " +
            "status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int update(Activity activity);

    @Delete("DELETE FROM activities WHERE id = #{id}")
    int deleteById(Long id);
}
