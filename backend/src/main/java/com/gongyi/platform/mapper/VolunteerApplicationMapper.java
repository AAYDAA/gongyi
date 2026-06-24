package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.VolunteerApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface VolunteerApplicationMapper {

    @Select("SELECT * FROM volunteer_applications WHERE id = #{id}")
    Optional<VolunteerApplication> findById(Long id);

    @Select("SELECT * FROM volunteer_applications WHERE user_id = #{userId}")
    Optional<VolunteerApplication> findByUserId(Long userId);

    @Select("<script>" +
            "SELECT va.* FROM volunteer_applications va LEFT JOIN users u ON va.user_id = u.id WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (va.real_name LIKE CONCAT('%',#{keyword},'%') OR va.contact_phone LIKE CONCAT('%',#{keyword},'%') OR va.motivation LIKE CONCAT('%',#{keyword},'%') OR u.username LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "<if test='status != null'> AND va.status = #{status}</if>" +
            " ORDER BY va.id DESC" +
            "</script>")
    List<VolunteerApplication> findByCondition(@Param("keyword") String keyword, @Param("status") String status);

    @Insert("INSERT INTO volunteer_applications (user_id, real_name, contact_phone, skills, experience, available_time, " +
            "motivation, status, review_remark, submit_time, review_time, create_time, update_time) VALUES " +
            "(#{userId}, #{realName}, #{contactPhone}, #{skills}, #{experience}, #{availableTime}, #{motivation}, " +
            "#{status}, #{reviewRemark}, #{submitTime}, #{reviewTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VolunteerApplication application);

    @Update("UPDATE volunteer_applications SET real_name = #{realName}, contact_phone = #{contactPhone}, " +
            "skills = #{skills}, experience = #{experience}, available_time = #{availableTime}, " +
            "motivation = #{motivation}, status = #{status}, review_remark = #{reviewRemark}, " +
            "review_time = #{reviewTime}, submit_time = #{submitTime}, update_time = #{updateTime} WHERE id = #{id}")
    int update(VolunteerApplication application);
}
