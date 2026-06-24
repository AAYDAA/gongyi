package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.OrganizationProfile;
import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.domain.enums.OrganizationVerifyStatus;
import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.dto.org.OrganizationProfileRequest;
import com.gongyi.platform.dto.org.OrganizationProfileResponse;
import com.gongyi.platform.dto.org.VerifyOrganizationRequest;
import com.gongyi.platform.mapper.OrganizationProfileMapper;
import com.gongyi.platform.service.OrganizationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationProfileMapper organizationProfileMapper;
    private final UserServiceImpl userService;

    public OrganizationServiceImpl(OrganizationProfileMapper organizationProfileMapper, UserServiceImpl userService) {
        this.organizationProfileMapper = organizationProfileMapper;
        this.userService = userService;
    }

    @Transactional
    @Override
    public OrganizationProfileResponse upsertMyProfile(Long userId, OrganizationProfileRequest request) {
        User user = userService.getEntityById(userId);
        if (user.getRole() != UserRole.ORGANIZATION) {
            throw new IllegalStateException("仅公益组织可维护组织信息");
        }
        if (!StringUtils.hasText(request.name())) {
            throw new IllegalStateException("组织名称不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        OrganizationProfile profile = organizationProfileMapper.findByUserId(userId).orElseGet(() -> {
            OrganizationProfile p = new OrganizationProfile();
            p.setUserId(userId);
            p.setUser(user);
            p.setVerifyStatus(OrganizationVerifyStatus.UNSUBMITTED);
            p.setCreateTime(now);
            return p;
        });

        profile.setName(request.name());
        if (request.logoUrl() != null) profile.setLogoUrl(request.logoUrl());
        if (request.intro() != null) profile.setIntro(request.intro());
        if (request.address() != null) profile.setAddress(request.address());
        if (request.contactPhone() != null) profile.setContactPhone(request.contactPhone());
        if (request.website() != null) profile.setWebsite(request.website());
        if (request.licenseImageUrls() != null) profile.setLicenseImageUrls(request.licenseImageUrls());
        profile.setUpdateTime(now);

        if (profile.getId() == null) {
            organizationProfileMapper.insert(profile);
        } else {
            organizationProfileMapper.update(profile);
        }
        profile.setUser(user);
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    @Override
    public OrganizationProfileResponse getMyProfile(Long userId) {
        OrganizationProfile profile = organizationProfileMapper.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("组织信息不存在"));
        profile.setUser(userService.getEntityById(profile.getUserId()));
        return toResponse(profile);
    }

    @Transactional
    @Override
    public OrganizationProfileResponse submitVerification(Long userId) {
        OrganizationProfile profile = organizationProfileMapper.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("请先完善组织信息"));
        profile.setVerifyStatus(OrganizationVerifyStatus.PENDING);
        profile.setVerifyRemark(null);
        profile.setVerifyTime(null);
        profile.setUpdateTime(LocalDateTime.now());
        organizationProfileMapper.update(profile);
        profile.setUser(userService.getEntityById(profile.getUserId()));
        return toResponse(profile);
    }

    @Transactional
    @Override
    public OrganizationProfileResponse verify(Long profileId, VerifyOrganizationRequest request) {
        OrganizationProfile profile = getEntity(profileId);
        if (request.verifyStatus() == null) {
            throw new IllegalStateException("审核状态不能为空");
        }
        if (request.verifyStatus() != OrganizationVerifyStatus.APPROVED && request.verifyStatus() != OrganizationVerifyStatus.REJECTED) {
            throw new IllegalStateException("审核状态不合法");
        }
        profile.setVerifyStatus(request.verifyStatus());
        profile.setVerifyRemark(request.verifyRemark());
        profile.setVerifyTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        organizationProfileMapper.update(profile);
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<OrganizationProfileResponse> page(String keyword, OrganizationVerifyStatus verifyStatus, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrganizationProfile> profiles = organizationProfileMapper.findByCondition(keyword,
                verifyStatus != null ? verifyStatus.name() : null);
        PageInfo<OrganizationProfile> pageInfo = new PageInfo<>(profiles);
        List<OrganizationProfileResponse> list = pageInfo.getList().stream().map(p -> {
            p.setUser(userService.getEntityById(p.getUserId()));
            return toResponse(p);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional(readOnly = true)
    @Override
    public OrganizationProfileResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    private OrganizationProfile getEntity(Long id) {
        OrganizationProfile profile = organizationProfileMapper.findById(id)
                .orElseThrow(() -> new IllegalStateException("组织信息不存在"));
        profile.setUser(userService.getEntityById(profile.getUserId()));
        return profile;
    }

    private OrganizationProfileResponse toResponse(OrganizationProfile profile) {
        return new OrganizationProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getName(),
                profile.getLogoUrl(),
                profile.getIntro(),
                profile.getAddress(),
                profile.getContactPhone(),
                profile.getWebsite(),
                profile.getLicenseImageUrls(),
                profile.getVerifyStatus(),
                profile.getVerifyRemark(),
                profile.getVerifyTime(),
                profile.getUser() != null ? userService.toResponse(profile.getUser()) : null,
                profile.getCreateTime(),
                profile.getUpdateTime()
        );
    }
}
