package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.ActivityApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ActivityApplicationMapper {

    @Select("SELECT * FROM activity_applications WHERE id = #{id}")
    Optional<ActivityApplication> findById(Long id);

    @Select("SELECT * FROM activity_applications WHERE activity_id = #{activityId} AND volunteer_id = #{volunteerId}")
    Optional<ActivityApplication> findByActivityIdAndVolunteerId(@Param("activityId") Long activityId, @Param("volunteerId") Long volunteerId);

    @Select("SELECT * FROM activity_applications WHERE activity_id = #{activityId} ORDER BY id DESC")
    List<ActivityApplication> findByActivityId(Long activityId);

    @Select("SELECT * FROM activity_applications WHERE volunteer_id = #{volunteerId} ORDER BY id DESC")
    List<ActivityApplication> findByVolunteerId(Long volunteerId);

    @Select("<script>" +
            "SELECT COUNT(*) > 0 FROM activity_applications WHERE activity_id = #{activityId} AND volunteer_id = #{volunteerId} AND status IN " +
            "<foreach collection='statuses' item='s' open='(' separator=',' close=')'>#{s}</foreach>" +
            "</script>")
    boolean existsByActivityIdAndVolunteerIdAndStatusIn(@Param("activityId") Long activityId, @Param("volunteerId") Long volunteerId,
                                                        @Param("statuses") List<String> statuses);

    @Insert("INSERT INTO activity_applications (activity_id, volunteer_id, apply_time, status, remark, create_time, update_time) " +
            "VALUES (#{activityId}, #{volunteerId}, #{applyTime}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityApplication application);

    @Update("UPDATE activity_applications SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateStatus(ActivityApplication application);
}
