package com.gongyi.platform.dto.org;

import com.gongyi.platform.domain.enums.OrganizationVerifyStatus;

public record VerifyOrganizationRequest(
        OrganizationVerifyStatus verifyStatus,
        String verifyRemark
) {
}
