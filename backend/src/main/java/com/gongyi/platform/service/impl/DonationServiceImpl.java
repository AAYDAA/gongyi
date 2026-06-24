package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.DonationRecord;
import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.dto.donation.DonationResponse;
import com.gongyi.platform.mapper.DonationRecordMapper;
import com.gongyi.platform.service.DonationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DonationServiceImpl implements DonationService {
    private final DonationRecordMapper donationRecordMapper;
    private final UserServiceImpl userService;

    public DonationServiceImpl(DonationRecordMapper donationRecordMapper, UserServiceImpl userService) {
        this.donationRecordMapper = donationRecordMapper;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<DonationResponse> myDonations(Long donorId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<DonationRecord> records = donationRecordMapper.findByDonorId(donorId);
        PageInfo<DonationRecord> pageInfo = new PageInfo<>(records);
        List<DonationResponse> list = pageInfo.getList().stream().map(this::toResponse).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<DonationResponse> projectDonations(Long projectId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<DonationRecord> records = donationRecordMapper.findByProjectId(projectId);
        PageInfo<DonationRecord> pageInfo = new PageInfo<>(records);
        List<DonationResponse> list = pageInfo.getList().stream().map(this::toResponse).toList();
        return new PageResult<>(list, pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    private DonationResponse toResponse(DonationRecord record) {
        User donor = record.getDonorId() != null ? userService.getEntityById(record.getDonorId()) : null;
        return new DonationResponse(
                record.getId(),
                record.getProjectId(),
                record.getProject() != null ? record.getProject().getTitle() : null,
                donor != null ? userService.toResponse(donor) : null,
                record.getAmount(),
                record.getChannel(),
                record.getStatus(),
                record.getDonateTime(),
                record.getRemark()
        );
    }
}
