package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.domain.entity.VolunteerApplication;
import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.domain.enums.VolunteerApplicationStatus;
import com.gongyi.platform.dto.volunteer.ReviewVolunteerApplicationRequest;
import com.gongyi.platform.dto.volunteer.VolunteerApplicationRequest;
import com.gongyi.platform.dto.volunteer.VolunteerApplicationResponse;
import com.gongyi.platform.mapper.UserMapper;
import com.gongyi.platform.mapper.VolunteerApplicationMapper;
import com.gongyi.platform.service.VolunteerApplicationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VolunteerApplicationServiceImpl implements VolunteerApplicationService {
    private final VolunteerApplicationMapper volunteerApplicationMapper;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    public VolunteerApplicationServiceImpl(
            VolunteerApplicationMapper volunteerApplicationMapper,
            UserServiceImpl userService,
            UserMapper userMapper
    ) {
        this.volunteerApplicationMapper = volunteerApplicationMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public VolunteerApplicationResponse getMine(Long userId) {
        VolunteerApplication application = volunteerApplicationMapper.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("志愿者申请不存在"));
        application.setUser(userService.getEntityById(application.getUserId()));
        return toResponse(application);
    }

    @Transactional
    @Override
    public VolunteerApplicationResponse upsertMine(Long userId, VolunteerApplicationRequest request) {
        User user = userService.getEntityById(userId);
        if (user.getRole() != UserRole.USER && user.getRole() != UserRole.VOLUNTEER) {
            throw new IllegalStateException("当前角色不可申请志愿者");
        }
        if (!StringUtils.hasText(request.realName()) || !StringUtils.hasText(request.contactPhone()) || !StringUtils.hasText(request.motivation())) {
            throw new IllegalStateException("请填写完整的必填信息");
        }
        LocalDateTime now = LocalDateTime.now();
        VolunteerApplication application = volunteerApplicationMapper.findByUserId(userId).orElseGet(() -> {
            VolunteerApplication a = new VolunteerApplication();
            a.setUserId(userId);
            a.setUser(user);
            a.setStatus(VolunteerApplicationStatus.DRAFT);
            a.setSubmitTime(now);
            a.setCreateTime(now);
            return a;
        });
        if (application.getStatus() == VolunteerApplicationStatus.PENDING) {
            throw new IllegalStateException("申请已提交，请等待管理员审核");
        }

        application.setRealName(request.realName().trim());
        application.setContactPhone(request.contactPhone().trim());
        application.setSkills(request.skills());
        application.setExperience(request.experience());
        application.setAvailableTime(request.availableTime());
        application.setMotivation(request.motivation().trim());
        if (application.getStatus() != VolunteerApplicationStatus.APPROVED) {
            application.setStatus(VolunteerApplicationStatus.DRAFT);
        }
        application.setReviewRemark(null);
        application.setReviewTime(null);
        application.setUpdateTime(now);

        if (application.getId() == null) {
            volunteerApplicationMapper.insert(application);
        } else {
            volunteerApplicationMapper.update(application);
        }
        application.setUser(user);
        return toResponse(application);
    }

    @Transactional
    @Override
    public VolunteerApplicationResponse submitMine(Long userId) {
        VolunteerApplication application = volunteerApplicationMapper.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("请先填写申请信息"));
        if (!StringUtils.hasText(application.getRealName()) || !StringUtils.hasText(application.getContactPhone()) || !StringUtils.hasText(application.getMotivation())) {
            throw new IllegalStateException("请先完善申请信息");
        }
        if (application.getStatus() == VolunteerApplicationStatus.PENDING) {
            throw new IllegalStateException("申请已提交，请勿重复提交");
        }
        application.setStatus(VolunteerApplicationStatus.PENDING);
        application.setSubmitTime(LocalDateTime.now());
        application.setReviewRemark(null);
        application.setReviewTime(null);
        application.setUpdateTime(LocalDateTime.now());
        volunteerApplicationMapper.update(application);
        application.setUser(userService.getEntityById(application.getUserId()));
        return toResponse(application);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<VolunteerApplicationResponse> page(String keyword, VolunteerApplicationStatus status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<VolunteerApplication> applications = volunteerApplicationMapper.findByCondition(keyword,
                status != null ? status.name() : null);
        PageInfo<VolunteerApplication> pageInfo = new PageInfo<>(applications);
        List<VolunteerApplicationResponse> list = pageInfo.getList().stream().map(a -> {
            a.setUser(userService.getEntityById(a.getUserId()));
            return toResponse(a);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional
    @Override
    public VolunteerApplicationResponse review(Long id, ReviewVolunteerApplicationRequest request) {
        VolunteerApplication application = volunteerApplicationMapper.findById(id)
                .orElseThrow(() -> new IllegalStateException("志愿者申请不存在"));
        if (request.status() == null) {
            throw new IllegalStateException("审核状态不能为空");
        }
        if (request.status() != VolunteerApplicationStatus.APPROVED && request.status() != VolunteerApplicationStatus.REJECTED) {
            throw new IllegalStateException("审核状态不合法");
        }
        if (application.getStatus() != VolunteerApplicationStatus.PENDING) {
            throw new IllegalStateException("仅待审核申请可处理");
        }
        application.setStatus(request.status());
        application.setReviewRemark(request.reviewRemark());
        application.setReviewTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        if (request.status() == VolunteerApplicationStatus.APPROVED) {
            User user = userService.getEntityById(application.getUserId());
            if (user.getRole() == UserRole.USER) {
                user.setRole(UserRole.VOLUNTEER);
                user.setUpdateTime(LocalDateTime.now());
                userMapper.update(user);
            }
        }
        volunteerApplicationMapper.update(application);
        application.setUser(userService.getEntityById(application.getUserId()));
        return toResponse(application);
    }

    private VolunteerApplicationResponse toResponse(VolunteerApplication application) {
        return new VolunteerApplicationResponse(
                application.getId(),
                application.getUserId(),
                application.getRealName(),
                application.getContactPhone(),
                application.getSkills(),
                application.getExperience(),
                application.getAvailableTime(),
                application.getMotivation(),
                application.getStatus(),
                application.getReviewRemark(),
                application.getSubmitTime(),
                application.getReviewTime(),
                application.getUser() != null ? userService.toResponse(application.getUser()) : null,
                application.getCreateTime(),
                application.getUpdateTime()
        );
    }
}
