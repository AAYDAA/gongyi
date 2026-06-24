package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.enums.OrganizationVerifyStatus;
import com.gongyi.platform.dto.org.OrganizationProfileRequest;
import com.gongyi.platform.dto.org.OrganizationProfileResponse;
import com.gongyi.platform.dto.org.VerifyOrganizationRequest;

public interface OrganizationService {
    OrganizationProfileResponse upsertMyProfile(Long userId, OrganizationProfileRequest request);

    OrganizationProfileResponse getMyProfile(Long userId);

    OrganizationProfileResponse submitVerification(Long userId);

    OrganizationProfileResponse verify(Long profileId, VerifyOrganizationRequest request);

    PageResult<OrganizationProfileResponse> page(String keyword, OrganizationVerifyStatus verifyStatus, int pageNum, int pageSize);

    OrganizationProfileResponse getById(Long id);
}
