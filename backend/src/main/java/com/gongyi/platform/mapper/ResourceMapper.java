package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.Resource;
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
public interface ResourceMapper {

    @Select("SELECT * FROM resources WHERE id = #{id}")
    Optional<Resource> findById(Long id);

    @Select("<script>" +
            "SELECT * FROM resources WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='publisherId != null'> AND publisher_id = #{publisherId}</if>" +
            " ORDER BY id DESC" +
            "</script>")
    List<Resource> findByCondition(@Param("keyword") String keyword, @Param("status") String status, @Param("publisherId") Long publisherId);

    @Insert("INSERT INTO resources (title, description, image_urls, status, publisher_id, publish_time, create_time, update_time) " +
            "VALUES (#{title}, #{description}, #{imageUrls}, #{status}, #{publisherId}, #{publishTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Resource resource);

    @Update("UPDATE resources SET title = #{title}, description = #{description}, image_urls = #{imageUrls}, " +
            "status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int update(Resource resource);

    @Delete("DELETE FROM resources WHERE id = #{id}")
    int deleteById(Long id);
}
