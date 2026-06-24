package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.dto.donation.DonationResponse;

public interface DonationService {
    PageResult<DonationResponse> myDonations(Long donorId, int pageNum, int pageSize);

    PageResult<DonationResponse> projectDonations(Long projectId, int pageNum, int pageSize);
}
