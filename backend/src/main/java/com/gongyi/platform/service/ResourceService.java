package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.enums.ResourceStatus;
import com.gongyi.platform.domain.enums.ResourceDonationStatus;
import com.gongyi.platform.dto.resource.ResourceDonationResponse;
import com.gongyi.platform.dto.resource.ResourceRequest;
import com.gongyi.platform.dto.resource.ResourceResponse;

public interface ResourceService {
    ResourceResponse create(ResourceRequest request, Long publisherId);

    ResourceResponse update(Long id, ResourceRequest request);

    void delete(Long id);

    ResourceResponse getById(Long id);

    PageResult<ResourceResponse> page(String keyword, ResourceStatus status, Long publisherId, int pageNum, int pageSize);

    ResourceDonationResponse claimResource(Long resourceId, Long volunteerId, Integer quantity, String remark);

    PageResult<ResourceDonationResponse> myClaims(Long volunteerId, int pageNum, int pageSize);

    PageResult<ResourceDonationResponse> pageClaims(ResourceDonationStatus status, int pageNum, int pageSize);

    ResourceDonationResponse reviewClaim(Long claimId, ResourceDonationStatus status);
}
