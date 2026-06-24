package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.*;
import com.gongyi.platform.domain.enums.ResourceDonationStatus;
import com.gongyi.platform.domain.enums.ResourceStatus;
import com.gongyi.platform.dto.resource.ResourceDonationResponse;
import com.gongyi.platform.dto.resource.ResourceRequest;
import com.gongyi.platform.dto.resource.ResourceResponse;
import com.gongyi.platform.mapper.ResourceDonationMapper;
import com.gongyi.platform.mapper.ResourceMapper;
import com.gongyi.platform.service.ResourceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceMapper resourceMapper;
    private final ResourceDonationMapper resourceDonationMapper;
    private final UserServiceImpl userService;

    public ResourceServiceImpl(ResourceMapper resourceMapper, ResourceDonationMapper resourceDonationMapper, UserServiceImpl userService) {
        this.resourceMapper = resourceMapper;
        this.resourceDonationMapper = resourceDonationMapper;
        this.userService = userService;
    }

    @Transactional
    @Override
    public ResourceResponse create(ResourceRequest request, Long publisherId) {
        User publisher = userService.getEntityById(publisherId);
        LocalDateTime now = LocalDateTime.now();
        Resource resource = new Resource();
        resource.setTitle(request.title());
        resource.setDescription(request.description());
        resource.setImageUrls(request.imageUrls());
        resource.setStatus(ResourceStatus.REQUESTING);
        resource.setPublisherId(publisherId);
        resource.setPublisher(publisher);
        resource.setPublishTime(now);
        resource.setCreateTime(now);
        resource.setUpdateTime(now);
        resourceMapper.insert(resource);
        return toResponse(resource);
    }

    @Transactional
    @Override
    public ResourceResponse update(Long id, ResourceRequest request) {
        Resource resource = getEntity(id);
        if (request.title() != null) resource.setTitle(request.title());
        if (request.description() != null) resource.setDescription(request.description());
        if (request.imageUrls() != null) resource.setImageUrls(request.imageUrls());
        if (request.status() != null) resource.setStatus(request.status());
        resource.setUpdateTime(LocalDateTime.now());
        resourceMapper.update(resource);
        resource.setPublisher(userService.getEntityById(resource.getPublisherId()));
        return toResponse(resource);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        resourceMapper.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public ResourceResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<ResourceResponse> page(String keyword, ResourceStatus status, Long publisherId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Resource> resources = resourceMapper.findByCondition(keyword,
                status != null ? status.name() : null, publisherId);
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);
        List<ResourceResponse> list = pageInfo.getList().stream().map(r -> {
            r.setPublisher(userService.getEntityById(r.getPublisherId()));
            return toResponse(r);
        }).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional
    @Override
    public ResourceDonationResponse claimResource(Long resourceId, Long volunteerId, Integer quantity, String remark) {
        Resource resource = getEntity(resourceId);
        if (resource.getStatus() != ResourceStatus.REQUESTING) {
            throw new IllegalStateException("当前资源不可认领");
        }
        int q = (quantity == null || quantity <= 0) ? 1 : quantity;

        User volunteer = userService.getEntityById(volunteerId);
        if (resourceDonationMapper.existsByResourceIdAndDonorId(resourceId, volunteerId)) {
            throw new IllegalStateException("请勿重复提交申请");
        }

        LocalDateTime now = LocalDateTime.now();
        ResourceDonation donation = new ResourceDonation();
        donation.setResourceId(resourceId);
        donation.setResource(resource);
        donation.setDonorId(volunteerId);
        donation.setDonor(volunteer);
        donation.setQuantity(q);
        donation.setStatus(ResourceDonationStatus.SUBMITTED);
        donation.setRemark(remark);
        donation.setDonateTime(now);
        donation.setCreateTime(now);
        donation.setUpdateTime(now);
        resourceDonationMapper.insert(donation);

        return toDonationResponse(donation);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<ResourceDonationResponse> myClaims(Long volunteerId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ResourceDonation> donations = resourceDonationMapper.findByDonorId(volunteerId);
        PageInfo<ResourceDonation> pageInfo = new PageInfo<>(donations);
        List<ResourceDonationResponse> list = pageInfo.getList().stream().map(this::toDonationResponse).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<ResourceDonationResponse> pageClaims(ResourceDonationStatus status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ResourceDonation> donations;
        if (status == null) {
            donations = resourceDonationMapper.findAll();
        } else {
            donations = resourceDonationMapper.findByStatus(status.name());
        }
        PageInfo<ResourceDonation> pageInfo = new PageInfo<>(donations);
        List<ResourceDonationResponse> list = pageInfo.getList().stream().map(this::toDonationResponse).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional
    @Override
    public ResourceDonationResponse reviewClaim(Long claimId, ResourceDonationStatus status) {
        if (status == null) {
            throw new IllegalStateException("审核状态不能为空");
        }
        if (status != ResourceDonationStatus.CONFIRMED && status != ResourceDonationStatus.REJECTED) {
            throw new IllegalStateException("审核状态不合法");
        }
        ResourceDonation donation = resourceDonationMapper.findById(claimId)
                .orElseThrow(() -> new IllegalStateException("认领记录不存在"));
        if (donation.getStatus() == ResourceDonationStatus.CONFIRMED || donation.getStatus() == ResourceDonationStatus.REJECTED) {
            throw new IllegalStateException("该申请已处理");
        }
        if (status == ResourceDonationStatus.CONFIRMED) {
            Resource resource = getEntity(donation.getResourceId());
            if (resource.getStatus() != ResourceStatus.REQUESTING) {
                throw new IllegalStateException("该资源已被认领");
            }
            resource.setStatus(ResourceStatus.CLAIMED);
            resource.setUpdateTime(LocalDateTime.now());
            resourceMapper.update(resource);

            LocalDateTime now = LocalDateTime.now();
            resourceDonationMapper.updateRejectedByResourceId(
                    donation.getResourceId(),
                    ResourceDonationStatus.REJECTED.name(),
                    now
            );
        }
        donation.setStatus(status);
        donation.setUpdateTime(LocalDateTime.now());
        resourceDonationMapper.updateStatus(donation);
        donation.setDonor(userService.getEntityById(donation.getDonorId()));
        donation.setResource(getEntity(donation.getResourceId()));
        return toDonationResponse(donation);
    }

    private Resource getEntity(Long id) {
        Resource resource = resourceMapper.findById(id).orElseThrow(() -> new IllegalStateException("资源不存在"));
        resource.setPublisher(userService.getEntityById(resource.getPublisherId()));
        return resource;
    }

    private ResourceResponse toResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getTitle(),
                resource.getDescription(),
                resource.getImageUrls(),
                resource.getStatus(),
                resource.getPublisher() != null ? userService.toResponse(resource.getPublisher()) : null,
                resource.getPublishTime(),
                resource.getCreateTime(),
                resource.getUpdateTime()
        );
    }

    private ResourceDonationResponse toDonationResponse(ResourceDonation donation) {
        if (donation.getResource() == null && donation.getResourceId() != null) {
            donation.setResource(resourceMapper.findById(donation.getResourceId()).orElse(null));
        }
        if (donation.getDonor() == null && donation.getDonorId() != null) {
            donation.setDonor(userService.getEntityById(donation.getDonorId()));
        }
        return new ResourceDonationResponse(
                donation.getId(),
                donation.getResourceId(),
                donation.getResource() != null ? donation.getResource().getTitle() : null,
                donation.getDonor() != null ? userService.toResponse(donation.getDonor()) : null,
                donation.getQuantity(),
                donation.getStatus(),
                donation.getRemark(),
                donation.getDonateTime()
        );
    }
}
