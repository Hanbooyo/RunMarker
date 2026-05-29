package com.stravamate.passport.dto.sync;

import com.stravamate.passport.domain.entity.SyncLog;

import java.time.Instant;

public record SyncJobStatusResponse(
        Long syncLogId,
        String mode,
        String status,
        int requestedCount,
        int syncedCount,
        int geocodedCount,
        int geocodingFailedCount,
        int skippedCount,
        String rateLimitLimit,
        String rateLimitUsage,
        String errorMessage,
        Instant startedAt,
        Instant finishedAt
) {
    public static SyncJobStatusResponse from(SyncLog syncLog) {
        return new SyncJobStatusResponse(
                syncLog.getId(),
                syncLog.getMode(),
                syncLog.getStatus(),
                valueOrZero(syncLog.getRequestedCount()),
                valueOrZero(syncLog.getSyncedCount()),
                valueOrZero(syncLog.getGeocodedCount()),
                valueOrZero(syncLog.getGeocodingFailedCount()),
                valueOrZero(syncLog.getSkippedCount()),
                syncLog.getRateLimitLimit(),
                syncLog.getRateLimitUsage(),
                syncLog.getErrorMessage(),
                syncLog.getStartedAt(),
                syncLog.getFinishedAt()
        );
    }

    private static int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
