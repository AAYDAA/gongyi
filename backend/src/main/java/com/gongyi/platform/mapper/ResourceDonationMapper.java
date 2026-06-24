package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.ResourceDonation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ResourceDonationMapper {

    @Select("SELECT * FROM resource_donations WHERE id = #{id}")
    Optional<ResourceDonation> findById(Long id);

    @Select("SELECT * FROM resource_donations WHERE donor_id = #{donorId} ORDER BY id DESC")
    List<ResourceDonation> findByDonorId(Long donorId);

    @Select("SELECT * FROM resource_donations WHERE status = #{status} ORDER BY id DESC")
    List<ResourceDonation> findByStatus(String status);

    @Select("SELECT * FROM resource_donations")
    List<ResourceDonation> findAll();

    @Select("SELECT * FROM resource_donations WHERE resource_id = #{resourceId} AND status = #{status}")
    List<ResourceDonation> findByResourceIdAndStatus(@Param("resourceId") Long resourceId, @Param("status") String status);

    @Select("SELECT COUNT(*) > 0 FROM resource_donations WHERE resource_id = #{resourceId} AND donor_id = #{donorId}")
    boolean existsByResourceIdAndDonorId(@Param("resourceId") Long resourceId, @Param("donorId") Long donorId);

    @Insert("INSERT INTO resource_donations (resource_id, donor_id, quantity, status, remark, donate_time, create_time, update_time) " +
            "VALUES (#{resourceId}, #{donorId}, #{quantity}, #{status}, #{remark}, #{donateTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ResourceDonation donation);

    @Update("UPDATE resource_donations SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateStatus(ResourceDonation donation);

    @Update("<script>" +
            "UPDATE resource_donations SET status = #{status}, update_time = #{updateTime} WHERE resource_id = #{resourceId} AND status = 'SUBMITTED'" +
            "</script>")
    int updateRejectedByResourceId(@Param("resourceId") Long resourceId, @Param("status") String status, @Param("updateTime") java.time.LocalDateTime updateTime);
}
