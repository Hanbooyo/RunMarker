package com.stravamate.passport.dto.activity;

import java.util.List;

public record ActivityListResponse(
        List<ActivityResponse> activities,
        int page,
        int size
) {
}
