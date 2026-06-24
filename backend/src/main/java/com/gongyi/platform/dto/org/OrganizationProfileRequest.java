package com.gongyi.platform.dto.org;

public record OrganizationProfileRequest(
        String name,
        String logoUrl,
        String intro,
        String address,
        String contactPhone,
        String website,
        String licenseImageUrls
) {
}
