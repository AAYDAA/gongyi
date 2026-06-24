package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.dto.user.UserRequest;
import com.gongyi.platform.dto.user.UserResponse;
import com.gongyi.platform.mapper.UserMapper;
import com.gongyi.platform.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    @Override
    public User getEntityById(Long id) {
        return userMapper.findById(id).orElseThrow(() -> new IllegalStateException("用户不存在"));
    }

    @Transactional
    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = getEntityById(id);
        if (request.username() != null) user.setUsername(request.username());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.email() != null) user.setEmail(request.email());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl());
        user.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.update(user);
        return toResponse(user);
    }

    @Transactional
    @Override
    public UserResponse applyVolunteer(Long id) {
        User user = getEntityById(id);
        if (user.getRole() == UserRole.VOLUNTEER) {
            return toResponse(user);
        }
        if (user.getRole() != UserRole.USER) {
            throw new IllegalStateException("当前角色不可申请为志愿者");
        }
        user.setRole(UserRole.VOLUNTEER);
        user.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.update(user);
        return toResponse(user);
    }

    @Override
    public PageResult<UserResponse> page(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.findAll();
        PageInfo<User> pageInfo = new PageInfo<>(users);
        List<UserResponse> list = pageInfo.getList().stream().map(this::toResponse).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getRegisterTime(),
                user.getStatus()
        );
    }
}
