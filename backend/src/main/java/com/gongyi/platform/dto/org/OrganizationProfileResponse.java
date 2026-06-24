package com.gongyi.platform.dto.org;

import com.gongyi.platform.domain.enums.OrganizationVerifyStatus;
import com.gongyi.platform.dto.user.UserResponse;

import java.time.LocalDateTime;

public record OrganizationProfileResponse(
        Long id,
        Long userId,
        String name,
        String logoUrl,
        String intro,
        String address,
        String contactPhone,
        String website,
        String licenseImageUrls,
        OrganizationVerifyStatus verifyStatus,
        String verifyRemark,
        LocalDateTime verifyTime,
        UserResponse user,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
