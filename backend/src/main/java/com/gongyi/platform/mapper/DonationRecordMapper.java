package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.DonationRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DonationRecordMapper {

    @Select("SELECT * FROM donation_records WHERE id = #{id}")
    DonationRecord findById(Long id);

    @Select("SELECT * FROM donation_records WHERE donor_id = #{donorId} ORDER BY id DESC")
    List<DonationRecord> findByDonorId(Long donorId);

    @Select("SELECT * FROM donation_records WHERE project_id = #{projectId} ORDER BY id DESC")
    List<DonationRecord> findByProjectId(Long projectId);

    @Select("<script>" +
            "SELECT * FROM donation_records WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (remark LIKE CONCAT('%',#{keyword},'%'))</if>" +
            " ORDER BY id DESC" +
            "</script>")
    List<DonationRecord> findByCondition(@Param("keyword") String keyword);

    @Insert("INSERT INTO donation_records (project_id, donor_id, amount, channel, status, donate_time, remark, create_time, update_time) " +
            "VALUES (#{projectId}, #{donorId}, #{amount}, #{channel}, #{status}, #{donateTime}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DonationRecord record);
}
