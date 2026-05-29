package com.stravamate.passport.service;

import com.stravamate.passport.client.strava.StravaActivitiesClient;
import com.stravamate.passport.domain.entity.Activity;
import com.stravamate.passport.domain.entity.SyncLog;
import com.stravamate.passport.dto.sync.SyncJobStartResponse;
import com.stravamate.passport.dto.sync.SyncJobStatusResponse;
import com.stravamate.passport.dto.strava.StravaActivityResponse;
import com.stravamate.passport.dto.sync.SyncActivitiesResponse;
import com.stravamate.passport.exception.GeocodingException;
import com.stravamate.passport.exception.ResourceNotFoundException;
import com.stravamate.passport.repository.ActivityRepository;
import com.stravamate.passport.repository.SyncLogRepository;
import com.stravamate.passport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ActivitySyncService {

    private static final String MODE_RECENT = "recent";
    private static final String MODE_FULL = "full";
    private static final int RECENT_SYNC_LIMIT = 100;
    private static final int PER_PAGE = 100;
    private static final int RATE_LIMIT_SAFETY_BUFFER = 5;

    private final TokenRefreshService tokenRefreshService;
    private final StravaActivitiesClient stravaActivitiesClient;
    private final ActivityRepository activityRepository;
    private final SyncLogRepository syncLogRepository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;
    private final Executor syncTaskExecutor;

    public ActivitySyncService(
            TokenRefreshService tokenRefreshService,
            StravaActivitiesClient stravaActivitiesClient,
            ActivityRepository activityRepository,
            SyncLogRepository syncLogRepository,
            UserRepository userRepository,
            GeocodingService geocodingService,
            @Qualifier("syncTaskExecutor") Executor syncTaskExecutor
    ) {
        this.tokenRefreshService = tokenRefreshService;
        this.stravaActivitiesClient = stravaActivitiesClient;
        this.activityRepository = activityRepository;
        this.syncLogRepository = syncLogRepository;
        this.userRepository = userRepository;
        this.geocodingService = geocodingService;
        this.syncTaskExecutor = syncTaskExecutor;
    }

    public SyncActivitiesResponse syncRecentActivities(Long userId) {
        return syncActivities(userId, MODE_RECENT);
    }

    public SyncActivitiesResponse syncActivities(Long userId, String mode) {
        String normalizedMode = normalizeMode(mode);
        SyncLog syncLog = syncLogRepository.insertStarted(userId, normalizedMode);
        return runSync(userId, normalizedMode, syncLog);
    }

    public SyncJobStartResponse startActivitiesSyncJob(Long userId, String mode) {
        String normalizedMode = normalizeMode(mode);
        SyncLog syncLog = syncLogRepository.insertStarted(userId, normalizedMode);

        CompletableFuture.runAsync(() -> runSync(userId, normalizedMode, syncLog), syncTaskExecutor);

        return new SyncJobStartResponse(syncLog.getId(), normalizedMode, syncLog.getStatus());
    }

    public SyncJobStatusResponse getSyncJobStatus(Long userId, Long syncLogId) {
        return syncLogRepository.findByIdAndUserId(syncLogId, userId)
                .map(SyncJobStatusResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("동기화 작업을 찾을 수 없습니다."));
    }

    private SyncActivitiesResponse runSync(Long userId, String normalizedMode, SyncLog syncLog) {

        int requestedCount = 0;
        int syncedCount = 0;
        int geocodedCount = 0;
        int geocodingFailedCount = 0;
        int skippedCount = 0;
        String lastRateLimitLimit = null;
        String lastRateLimitUsage = null;
        boolean stoppedNearRateLimit = false;

        try {
            String accessToken = tokenRefreshService.getValidAccessToken(userId);

            for (int page = 1; shouldContinuePaging(normalizedMode, requestedCount); page++) {
                int perPage = resolvePerPage(normalizedMode, requestedCount);

                StravaActivitiesClient.StravaActivitiesPage activitiesPage =
                        stravaActivitiesClient.getAthleteActivities(accessToken, page, perPage);

                List<StravaActivityResponse> activities = activitiesPage.activities();
                lastRateLimitLimit = activitiesPage.rateLimitLimit();
                lastRateLimitUsage = activitiesPage.rateLimitUsage();

                if (activities.isEmpty()) {
                    break;
                }

                requestedCount += activities.size();

                for (StravaActivityResponse stravaActivity : activities) {
                    try {
                        if (!stravaActivity.isRun() || !stravaActivity.hasStartLatlng()) {
                            skippedCount++;
                            continue;
                        }

                        var insertedActivity = activityRepository.insertIfNotExists(toActivity(userId, stravaActivity));
                        boolean inserted = insertedActivity.isPresent();

                        if (inserted) {
                            if (tryGeocode(insertedActivity.get())) {
                                geocodedCount++;
                            } else {
                                geocodingFailedCount++;
                            }
                            syncedCount++;
                        } else {
                            var existingActivity = activityRepository.findByUserIdAndStravaActivityId(userId, stravaActivity.id());
                            if (existingActivity.isPresent() && existingActivity.get().getGeocodedAt() == null) {
                                if (tryGeocode(existingActivity.get())) {
                                    geocodedCount++;
                                } else {
                                    geocodingFailedCount++;
                                }
                            }
                            skippedCount++;
                        }
                    } catch (Exception exception) {
                        skippedCount++;
                    }
                }

                syncLogRepository.updateProgress(
                        syncLog.getId(),
                        requestedCount,
                        syncedCount,
                        geocodedCount,
                        geocodingFailedCount,
                        skippedCount,
                        lastRateLimitLimit,
                        lastRateLimitUsage
                );

                if (activities.size() < perPage) {
                    break;
                }

                if (isNearRateLimit(lastRateLimitLimit, lastRateLimitUsage)) {
                    stoppedNearRateLimit = true;
                    break;
                }
            }

            userRepository.updateLastSyncedAt(userId);
            String status = geocodingFailedCount > 0 || stoppedNearRateLimit ? "PARTIAL_SUCCESS" : "SUCCESS";
            syncLogRepository.finish(
                    syncLog.getId(),
                    status,
                    requestedCount,
                    syncedCount,
                    geocodedCount,
                    geocodingFailedCount,
                    skippedCount,
                    lastRateLimitLimit,
                    lastRateLimitUsage,
                    null
            );

            return new SyncActivitiesResponse(
                    normalizedMode,
                    requestedCount,
                    syncedCount,
                    geocodedCount,
                    geocodingFailedCount,
                    skippedCount,
                    status,
                    lastRateLimitLimit,
                    lastRateLimitUsage
            );
        } catch (Exception exception) {
            syncLogRepository.finish(
                    syncLog.getId(),
                    "FAILED",
                    requestedCount,
                    syncedCount,
                    geocodedCount,
                    geocodingFailedCount,
                    skippedCount,
                    lastRateLimitLimit,
                    lastRateLimitUsage,
                    exception.getMessage()
            );
            throw exception;
        }
    }

    private String normalizeMode(String mode) {
        if (MODE_FULL.equalsIgnoreCase(mode)) {
            return MODE_FULL;
        }
        return MODE_RECENT;
    }

    private boolean shouldContinuePaging(String mode, int requestedCount) {
        return MODE_FULL.equals(mode) || requestedCount < RECENT_SYNC_LIMIT;
    }

    private int resolvePerPage(String mode, int requestedCount) {
        if (MODE_FULL.equals(mode)) {
            return PER_PAGE;
        }
        return Math.min(PER_PAGE, RECENT_SYNC_LIMIT - requestedCount);
    }

    private boolean isNearRateLimit(String limitHeader, String usageHeader) {
        int[] limits = parseRateLimitHeader(limitHeader);
        int[] usages = parseRateLimitHeader(usageHeader);
        if (limits.length < 2 || usages.length < 2) {
            return false;
        }

        return usages[0] >= limits[0] - RATE_LIMIT_SAFETY_BUFFER
                || usages[1] >= limits[1] - RATE_LIMIT_SAFETY_BUFFER;
    }

    private int[] parseRateLimitHeader(String value) {
        if (value == null || value.isBlank()) {
            return new int[0];
        }

        String[] parts = value.split(",");
        int[] parsed = new int[parts.length];
        try {
            for (int index = 0; index < parts.length; index++) {
                parsed[index] = Integer.parseInt(parts[index].trim());
            }
            return parsed;
        } catch (NumberFormatException exception) {
            return new int[0];
        }
    }

    private boolean tryGeocode(Activity activity) {
        try {
            return geocodingService.geocodeAndAttachPlace(activity);
        } catch (GeocodingException exception) {
            return false;
        }
    }

    private Activity toActivity(Long userId, StravaActivityResponse stravaActivity) {
        Activity activity = new Activity();
        activity.setUserId(userId);
        activity.setStravaActivityId(stravaActivity.id());
        activity.setName(stravaActivity.name());
        activity.setType(stravaActivity.type());
        activity.setSportType(stravaActivity.sportType());
        activity.setStartDate(stravaActivity.startDate());
        activity.setStartDateLocal(stravaActivity.parsedStartDateLocal());
        activity.setTimezone(stravaActivity.timezone());
        activity.setDistanceMeters(stravaActivity.distance());
        activity.setMovingTimeSeconds(stravaActivity.movingTime());
        activity.setElapsedTimeSeconds(stravaActivity.elapsedTime());
        activity.setStartLatitude(stravaActivity.startLatitude());
        activity.setStartLongitude(stravaActivity.startLongitude());
        return activity;
    }
}
