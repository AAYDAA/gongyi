package com.gongyi.platform.dto.volunteer;

import com.gongyi.platform.domain.enums.VolunteerApplicationStatus;

public record ReviewVolunteerApplicationRequest(
        VolunteerApplicationStatus status,
        String reviewRemark
) {
}
