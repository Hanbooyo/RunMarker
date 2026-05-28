package com.stravamate.passport.dto.sync;

public record SyncActivitiesResponse(
        int requestedCount,
        int syncedCount,
        int geocodedCount,
        int geocodingFailedCount,
        int skippedCount,
        String status,
        String rateLimitLimit,
        String rateLimitUsage
) {
}
