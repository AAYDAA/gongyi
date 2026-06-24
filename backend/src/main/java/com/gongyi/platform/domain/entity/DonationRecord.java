package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.DonationChannel;
import com.gongyi.platform.domain.enums.DonationStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DonationRecord {
    private Long id;
    private Long projectId;
    private CharityProject project;
    private Long donorId;
    private User donor;
    private BigDecimal amount;
    private DonationChannel channel;
    private DonationStatus status;
    private LocalDateTime donateTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
