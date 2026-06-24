package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.enums.ProjectStatus;
import com.gongyi.platform.dto.donation.DonationResponse;
import com.gongyi.platform.dto.project.DonateProjectRequest;
import com.gongyi.platform.dto.project.ProjectRequest;
import com.gongyi.platform.dto.project.ProjectResponse;

public interface ProjectService {
    ProjectResponse create(ProjectRequest request, Long creatorId);

    ProjectResponse update(Long id, ProjectRequest request);

    void delete(Long id);

    ProjectResponse getById(Long id);

    PageResult<ProjectResponse> page(String keyword, ProjectStatus status, Long creatorId, int pageNum, int pageSize);

    DonationResponse donate(Long projectId, Long donorId, DonateProjectRequest request);
}
