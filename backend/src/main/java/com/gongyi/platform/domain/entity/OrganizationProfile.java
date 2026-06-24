package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.OrganizationVerifyStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrganizationProfile {
    private Long id;
    private Long userId;
    private User user;
    private String name;
    private String logoUrl;
    private String intro;
    private String address;
    private String contactPhone;
    private String website;
    private String licenseImageUrls;
    private OrganizationVerifyStatus verifyStatus;
    private String verifyRemark;
    private LocalDateTime verifyTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
