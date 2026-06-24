package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.enums.ActivityStatus;
import com.gongyi.platform.domain.enums.ActivityApplicationStatus;
import com.gongyi.platform.dto.activity.ActivityRequest;
import com.gongyi.platform.dto.activity.ActivityResponse;
import com.gongyi.platform.dto.activity.ApplicationResponse;
import com.gongyi.platform.dto.activity.MyApplicationResponse;

public interface ActivityService {
    ActivityResponse create(ActivityRequest request, Long creatorId);

    ActivityResponse update(Long id, ActivityRequest request);

    void delete(Long id);

    ActivityResponse getById(Long id);

    PageResult<ActivityResponse> page(String keyword, String location, ActivityStatus status, Long creatorId, int pageNum, int pageSize);

    ApplicationResponse applyActivity(Long activityId, Long userId, String remark);

    PageResult<ActivityResponse> getMyPublishedActivities(Long creatorId, int pageNum, int pageSize);

    ActivityResponse reviewActivity(Long activityId, ActivityStatus status);

    PageResult<ApplicationResponse> pageApplications(Long activityId, Long operatorId, int pageNum, int pageSize);

    PageResult<MyApplicationResponse> myApplications(Long userId, int pageNum, int pageSize);

    ApplicationResponse reviewApplication(Long applicationId, Long operatorId, ActivityApplicationStatus status);

    ApplicationResponse cancelMyApplication(Long applicationId, Long userId);
}
