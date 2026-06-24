package com.gongyi.platform.dto.volunteer;

public record VolunteerApplicationRequest(
        String realName,
        String contactPhone,
        String skills,
        String experience,
        String availableTime,
        String motivation
) {
}
