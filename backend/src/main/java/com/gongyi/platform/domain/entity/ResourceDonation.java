package com.gongyi.platform.domain.entity;

import com.gongyi.platform.domain.enums.ResourceDonationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResourceDonation {
    private Long id;
    private Long resourceId;
    private Resource resource;
    private Long donorId;
    private User donor;
    private Integer quantity;
    private ResourceDonationStatus status;
    private String remark;
    private LocalDateTime donateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
