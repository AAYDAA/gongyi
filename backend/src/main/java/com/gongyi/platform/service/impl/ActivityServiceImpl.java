package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.*;
import com.gongyi.platform.domain.enums.ActivityApplicationStatus;
import com.gongyi.platform.domain.enums.ActivityStatus;
import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.dto.activity.*;
import com.gongyi.platform.mapper.ActivityApplicationMapper;
import com.gongyi.platform.mapper.ActivityMapper;
import com.gongyi.platform.service.ActivityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityMapper activityMapper;
    private final ActivityApplicationMapper activityApplicationMapper;
    private final UserServiceImpl userService;

    public ActivityServiceImpl(
            ActivityMapper activityMapper,
            ActivityApplicationMapper activityApplicationMapper,
            UserServiceImpl userService
    ) {
        this.activityMapper = activityMapper;
        this.activityApplicationMapper = activityApplicationMapper;
        this.userService = userService;
    }

    @Transactional
    @Override
    public ActivityResponse create(ActivityRequest request, Long creatorId) {
        User creator = userService.getEntityById(creatorId);
        LocalDateTime now = LocalDateTime.now();
        Activity activity = new Activity();
        activity.setTitle(request.title());
        activity.setCoverImage(request.coverImage());
        activity.setContentHtml(request.contentHtml());
        activity.setLocation(request.location());
        activity.setStartTime(request.startTime());
        activity.setEndTime(request.endTime());
        activity.setDeadlineTime(request.deadlineTime());
        activity.setStatus(request.status() != null ? request.status() : ActivityStatus.DRAFT);
        activity.setCreatorId(creatorId);
        activity.setCreator(creator);
        activity.setCreateTime(now);
        activity.setUpdateTime(now);
        activityMapper.insert(activity);
        return toResponse(activity);
    }

    @Transactional
    @Override
    public ActivityResponse update(Long id, ActivityRequest request) {
        Activity activity = getEntity(id);
        if (request.title() != null) activity.setTitle(request.title());
        if (request.coverImage() != null) activity.setCoverImage(request.coverImage());
        if (request.contentHtml() != null) activity.setContentHtml(request.contentHtml());
        if (request.location() != null) activity.setLocation(request.location());
        if (request.startTime() != null) activity.setStartTime(request.startTime());
        if (request.endTime() != null) activity.setEndTime(request.endTime());
        if (request.deadlineTime() != null) activity.setDeadlineTime(request.deadlineTime());
        if (request.status() != null) activity.setStatus(request.status());
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.update(activity);
        activity.setCreator(userService.getEntityById(activity.getCreatorId()));
        return toResponse(activity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        activityMapper.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public ActivityResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<ActivityResponse> page(String keyword, String location, ActivityStatus status, Long creatorId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Activity> activities = activityMapper.findByCondition(keyword, location,
                status != null ? status.name() : null, creatorId);
        PageInfo<Activity> pageInfo = new PageInfo<>(activities);
        List<ActivityResponse> list = pageInfo.getList().stream().map(a -> {
            a.setCreator(userService.getEntityById(a.getCreatorId()));
            return toResponse(a);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional
    @Override
    public ApplicationResponse applyActivity(Long activityId, Long userId, String remark) {
        Activity activity = getEntity(activityId);
        if (activity.getStatus() != ActivityStatus.RECRUITING) {
            throw new IllegalStateException("当前活动不可报名");
        }
        if (activity.getDeadlineTime() != null && LocalDateTime.now().isAfter(activity.getDeadlineTime())) {
            throw new IllegalStateException("报名已截止");
        }
        boolean existsActive = activityApplicationMapper.existsByActivityIdAndVolunteerIdAndStatusIn(
                activityId, userId,
                Arrays.asList(ActivityApplicationStatus.PENDING.name(), ActivityApplicationStatus.APPROVED.name())
        );
        if (existsActive) {
            throw new IllegalStateException("请勿重复报名");
        }

        User volunteer = userService.getEntityById(userId);
        LocalDateTime now = LocalDateTime.now();
        ActivityApplication application = new ActivityApplication();
        application.setActivityId(activityId);
        application.setActivity(activity);
        application.setVolunteerId(userId);
        application.setVolunteer(volunteer);
        application.setApplyTime(now);
        application.setStatus(ActivityApplicationStatus.PENDING);
        application.setRemark(remark);
        application.setCreateTime(now);
        application.setUpdateTime(now);
        activityApplicationMapper.insert(application);
        return toResponse(application);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<ActivityResponse> getMyPublishedActivities(Long creatorId, int pageNum, int pageSize) {
        return page(null, null, null, creatorId, pageNum, pageSize);
    }

    @Transactional
    @Override
    public ActivityResponse reviewActivity(Long activityId, ActivityStatus status) {
        if (status == null) {
            throw new IllegalStateException("审核状态不能为空");
        }
        Activity activity = getEntity(activityId);
        activity.setStatus(status);
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.update(activity);
        activity.setCreator(userService.getEntityById(activity.getCreatorId()));
        return toResponse(activity);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<ApplicationResponse> pageApplications(Long activityId, Long operatorId, int pageNum, int pageSize) {
        Activity activity = getEntity(activityId);
        User operator = userService.getEntityById(operatorId);
        if (operator.getRole() != UserRole.ADMIN && !activity.getCreatorId().equals(operatorId)) {
            throw new IllegalStateException("无权查看该活动报名信息");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<ActivityApplication> applications = activityApplicationMapper.findByActivityId(activityId);
        PageInfo<ActivityApplication> pageInfo = new PageInfo<>(applications);
        List<ApplicationResponse> list = pageInfo.getList().stream().map(a -> {
            a.setVolunteer(userService.getEntityById(a.getVolunteerId()));
            return toResponse(a);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<MyApplicationResponse> myApplications(Long userId, int pageNum, int pageSize) {
        User user = userService.getEntityById(userId);
        if (user.getRole() != UserRole.VOLUNTEER) {
            throw new IllegalStateException("仅志愿者可查看报名记录");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<ActivityApplication> applications = activityApplicationMapper.findByVolunteerId(userId);
        PageInfo<ActivityApplication> pageInfo = new PageInfo<>(applications);
        List<MyApplicationResponse> list = pageInfo.getList().stream().map(a -> {
            a.setActivity(getEntity(a.getActivityId()));
            return toMyApplicationResponse(a);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional
    @Override
    public ApplicationResponse reviewApplication(Long applicationId, Long operatorId, ActivityApplicationStatus status) {
        if (status == null) {
            throw new IllegalStateException("审核状态不能为空");
        }
        if (status != ActivityApplicationStatus.APPROVED && status != ActivityApplicationStatus.REJECTED) {
            throw new IllegalStateException("审核状态不合法");
        }
        ActivityApplication application = activityApplicationMapper.findById(applicationId)
                .orElseThrow(() -> new IllegalStateException("报名记录不存在"));
        Activity activity = getEntity(application.getActivityId());
        User operator = userService.getEntityById(operatorId);
        if (operator.getRole() != UserRole.ADMIN && !activity.getCreatorId().equals(operatorId)) {
            throw new IllegalStateException("无权审核该报名");
        }
        if (application.getStatus() != ActivityApplicationStatus.PENDING) {
            throw new IllegalStateException("该报名已处理");
        }
        application.setStatus(status);
        application.setUpdateTime(LocalDateTime.now());
        activityApplicationMapper.updateStatus(application);
        application.setVolunteer(userService.getEntityById(application.getVolunteerId()));
        return toResponse(application);
    }

    @Transactional
    @Override
    public ApplicationResponse cancelMyApplication(Long applicationId, Long userId) {
        ActivityApplication application = activityApplicationMapper.findById(applicationId)
                .orElseThrow(() -> new IllegalStateException("报名记录不存在"));
        if (!application.getVolunteerId().equals(userId)) {
            throw new IllegalStateException("无权取消该报名");
        }
        if (application.getStatus() != ActivityApplicationStatus.PENDING && application.getStatus() != ActivityApplicationStatus.APPROVED) {
            throw new IllegalStateException("当前状态不可取消");
        }
        application.setStatus(ActivityApplicationStatus.CANCELLED);
        application.setUpdateTime(LocalDateTime.now());
        activityApplicationMapper.updateStatus(application);
        application.setVolunteer(userService.getEntityById(application.getVolunteerId()));
        return toResponse(application);
    }

    private Activity getEntity(Long id) {
        Activity activity = activityMapper.findById(id).orElseThrow(() -> new IllegalStateException("活动不存在"));
        activity.setCreator(userService.getEntityById(activity.getCreatorId()));
        return activity;
    }

    private ActivityResponse toResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getTitle(),
                activity.getCoverImage(),
                activity.getContentHtml(),
                activity.getLocation(),
                activity.getStartTime(),
                activity.getEndTime(),
                activity.getDeadlineTime(),
                activity.getStatus(),
                activity.getCreator() != null ? userService.toResponse(activity.getCreator()) : null,
                activity.getCreateTime(),
                activity.getUpdateTime()
        );
    }

    private ApplicationResponse toResponse(ActivityApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getActivityId(),
                application.getVolunteer() != null ? userService.toResponse(application.getVolunteer()) : null,
                application.getApplyTime(),
                application.getStatus(),
                application.getRemark()
        );
    }

    private MyApplicationResponse toMyApplicationResponse(ActivityApplication application) {
        Activity activity = application.getActivity();
        return new MyApplicationResponse(
                application.getId(),
                activity.getId(),
                activity.getTitle(),
                activity.getLocation(),
                activity.getStartTime(),
                activity.getEndTime(),
                application.getApplyTime(),
                application.getStatus(),
                application.getRemark()
        );
    }
}
