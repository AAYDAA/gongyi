package com.gongyi.platform.service;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.dto.user.UserRequest;
import com.gongyi.platform.dto.user.UserResponse;

public interface UserService {
    UserResponse getById(Long id);

    User getEntityById(Long id);

    UserResponse update(Long id, UserRequest request);

    UserResponse applyVolunteer(Long id);

    PageResult<UserResponse> page(int pageNum, int pageSize);
}
