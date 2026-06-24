package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.OrganizationProfile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrganizationProfileMapper {

    @Select("SELECT * FROM organization_profiles WHERE id = #{id}")
    Optional<OrganizationProfile> findById(Long id);

    @Select("SELECT * FROM organization_profiles WHERE user_id = #{userId}")
    Optional<OrganizationProfile> findByUserId(Long userId);

    @Select("<script>" +
            "SELECT * FROM organization_profiles WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (name LIKE CONCAT('%',#{keyword},'%') OR intro LIKE CONCAT('%',#{keyword},'%') OR address LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "<if test='verifyStatus != null'> AND verify_status = #{verifyStatus}</if>" +
            " ORDER BY id DESC" +
            "</script>")
    List<OrganizationProfile> findByCondition(@Param("keyword") String keyword, @Param("verifyStatus") String verifyStatus);

    @Insert("INSERT INTO organization_profiles (user_id, name, logo_url, intro, address, contact_phone, website, " +
            "license_image_urls, verify_status, verify_remark, verify_time, create_time, update_time) VALUES " +
            "(#{userId}, #{name}, #{logoUrl}, #{intro}, #{address}, #{contactPhone}, #{website}, #{licenseImageUrls}, " +
            "#{verifyStatus}, #{verifyRemark}, #{verifyTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrganizationProfile profile);

    @Update("UPDATE organization_profiles SET name = #{name}, logo_url = #{logoUrl}, intro = #{intro}, " +
            "address = #{address}, contact_phone = #{contactPhone}, website = #{website}, " +
            "license_image_urls = #{licenseImageUrls}, verify_status = #{verifyStatus}, verify_remark = #{verifyRemark}, " +
            "verify_time = #{verifyTime}, update_time = #{updateTime} WHERE id = #{id}")
    int update(OrganizationProfile profile);
}
