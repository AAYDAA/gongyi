package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.*;
import com.gongyi.platform.domain.enums.DonationChannel;
import com.gongyi.platform.domain.enums.DonationStatus;
import com.gongyi.platform.domain.enums.ProjectStatus;
import com.gongyi.platform.dto.donation.DonationResponse;
import com.gongyi.platform.dto.project.DonateProjectRequest;
import com.gongyi.platform.dto.project.ProjectRequest;
import com.gongyi.platform.dto.project.ProjectResponse;
import com.gongyi.platform.mapper.CharityProjectMapper;
import com.gongyi.platform.mapper.DonationRecordMapper;
import com.gongyi.platform.service.ProjectService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final CharityProjectMapper charityProjectMapper;
    private final DonationRecordMapper donationRecordMapper;
    private final UserServiceImpl userService;

    public ProjectServiceImpl(CharityProjectMapper charityProjectMapper, DonationRecordMapper donationRecordMapper, UserServiceImpl userService) {
        this.charityProjectMapper = charityProjectMapper;
        this.donationRecordMapper = donationRecordMapper;
        this.userService = userService;
    }

    @Transactional
    @Override
    public ProjectResponse create(ProjectRequest request, Long creatorId) {
        User creator = userService.getEntityById(creatorId);
        if (!StringUtils.hasText(request.title())) {
            throw new IllegalStateException("项目标题不能为空");
        }
        BigDecimal goal = request.goalAmount() != null ? request.goalAmount() : BigDecimal.ZERO;
        if (goal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("目标金额必须大于0");
        }

        LocalDateTime now = LocalDateTime.now();
        CharityProject project = new CharityProject();
        project.setCreatorId(creatorId);
        project.setCreator(creator);
        project.setTitle(request.title());
        project.setCoverImage(request.coverImage());
        project.setSummary(request.summary());
        project.setContentHtml(request.contentHtml());
        project.setGoalAmount(goal);
        project.setRaisedAmount(BigDecimal.ZERO);
        project.setStatus(request.status() != null ? request.status() : ProjectStatus.DRAFT);
        project.setStartTime(request.startTime());
        project.setEndTime(request.endTime());
        project.setCreateTime(now);
        project.setUpdateTime(now);
        charityProjectMapper.insert(project);
        return toResponse(project);
    }

    @Transactional
    @Override
    public ProjectResponse update(Long id, ProjectRequest request) {
        CharityProject project = getEntity(id);
        if (request.title() != null) project.setTitle(request.title());
        if (request.coverImage() != null) project.setCoverImage(request.coverImage());
        if (request.summary() != null) project.setSummary(request.summary());
        if (request.contentHtml() != null) project.setContentHtml(request.contentHtml());
        if (request.goalAmount() != null) project.setGoalAmount(request.goalAmount());
        if (request.status() != null) project.setStatus(request.status());
        if (request.startTime() != null) project.setStartTime(request.startTime());
        if (request.endTime() != null) project.setEndTime(request.endTime());
        project.setUpdateTime(LocalDateTime.now());
        charityProjectMapper.update(project);
        project.setCreator(userService.getEntityById(project.getCreatorId()));
        return toResponse(project);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        charityProjectMapper.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<ProjectResponse> page(String keyword, ProjectStatus status, Long creatorId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CharityProject> projects = charityProjectMapper.findByCondition(keyword,
                status != null ? status.name() : null, creatorId);
        PageInfo<CharityProject> pageInfo = new PageInfo<>(projects);
        List<ProjectResponse> list = pageInfo.getList().stream().map(p -> {
            p.setCreator(userService.getEntityById(p.getCreatorId()));
            return toResponse(p);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional
    @Override
    public DonationResponse donate(Long projectId, Long donorId, DonateProjectRequest request) {
        CharityProject project = getEntity(projectId);
        if (project.getStatus() != ProjectStatus.ACTIVE) {
            throw new IllegalStateException("当前项目不可捐款");
        }

        BigDecimal amount = request.amount() != null ? request.amount() : BigDecimal.ZERO;
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("捐款金额必须大于0");
        }

        DonationChannel channel = request.channel() != null ? request.channel() : DonationChannel.WECHAT;
        User donor = userService.getEntityById(donorId);
        LocalDateTime now = LocalDateTime.now();

        DonationRecord record = new DonationRecord();
        record.setProjectId(projectId);
        record.setProject(project);
        record.setDonorId(donorId);
        record.setDonor(donor);
        record.setAmount(amount);
        record.setChannel(channel);
        record.setStatus(DonationStatus.SUCCESS);
        record.setDonateTime(now);
        record.setRemark(request.remark());
        record.setCreateTime(now);
        record.setUpdateTime(now);
        donationRecordMapper.insert(record);

        project.setRaisedAmount(project.getRaisedAmount().add(amount));
        if (project.getRaisedAmount().compareTo(project.getGoalAmount()) >= 0) {
            project.setStatus(ProjectStatus.COMPLETED);
        }
        project.setUpdateTime(LocalDateTime.now());
        charityProjectMapper.update(project);

        return new DonationResponse(
                record.getId(),
                project.getId(),
                project.getTitle(),
                userService.toResponse(donor),
                record.getAmount(),
                record.getChannel(),
                record.getStatus(),
                record.getDonateTime(),
                record.getRemark()
        );
    }

    private CharityProject getEntity(Long id) {
        CharityProject project = charityProjectMapper.findById(id).orElseThrow(() -> new IllegalStateException("项目不存在"));
        project.setCreator(userService.getEntityById(project.getCreatorId()));
        return project;
    }

    private ProjectResponse toResponse(CharityProject project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getCoverImage(),
                project.getSummary(),
                project.getContentHtml(),
                project.getGoalAmount(),
                project.getRaisedAmount(),
                project.getStatus(),
                project.getStartTime(),
                project.getEndTime(),
                project.getCreator() != null ? userService.toResponse(project.getCreator()) : null,
                project.getCreateTime(),
                project.getUpdateTime()
        );
    }
}
