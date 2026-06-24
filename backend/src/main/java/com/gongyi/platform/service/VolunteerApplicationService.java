package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.enums.VolunteerApplicationStatus;
import com.gongyi.platform.dto.volunteer.ReviewVolunteerApplicationRequest;
import com.gongyi.platform.dto.volunteer.VolunteerApplicationRequest;
import com.gongyi.platform.dto.volunteer.VolunteerApplicationResponse;

public interface VolunteerApplicationService {
    VolunteerApplicationResponse getMine(Long userId);

    VolunteerApplicationResponse upsertMine(Long userId, VolunteerApplicationRequest request);

    VolunteerApplicationResponse submitMine(Long userId);

    PageResult<VolunteerApplicationResponse> page(String keyword, VolunteerApplicationStatus status, int pageNum, int pageSize);

    VolunteerApplicationResponse review(Long id, ReviewVolunteerApplicationRequest request);
}
