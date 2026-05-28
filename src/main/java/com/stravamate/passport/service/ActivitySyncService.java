package com.stravamate.passport.service;

import com.stravamate.passport.client.strava.StravaActivitiesClient;
import com.stravamate.passport.domain.entity.Activity;
import com.stravamate.passport.domain.entity.SyncLog;
import com.stravamate.passport.dto.strava.StravaActivityResponse;
import com.stravamate.passport.dto.sync.SyncActivitiesResponse;
import com.stravamate.passport.exception.GeocodingException;
import com.stravamate.passport.repository.ActivityRepository;
import com.stravamate.passport.repository.SyncLogRepository;
import com.stravamate.passport.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivitySyncService {

    private static final int FIRST_SYNC_LIMIT = 100;
    private static final int PER_PAGE = 50;

    private final TokenRefreshService tokenRefreshService;
    private final StravaActivitiesClient stravaActivitiesClient;
    private final ActivityRepository activityRepository;
    private final SyncLogRepository syncLogRepository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;

    public ActivitySyncService(
            TokenRefreshService tokenRefreshService,
            StravaActivitiesClient stravaActivitiesClient,
            ActivityRepository activityRepository,
            SyncLogRepository syncLogRepository,
            UserRepository userRepository,
            GeocodingService geocodingService
    ) {
        this.tokenRefreshService = tokenRefreshService;
        this.stravaActivitiesClient = stravaActivitiesClient;
        this.activityRepository = activityRepository;
        this.syncLogRepository = syncLogRepository;
        this.userRepository = userRepository;
        this.geocodingService = geocodingService;
    }

    public SyncActivitiesResponse syncRecentActivities(Long userId) {
        SyncLog syncLog = syncLogRepository.insertStarted(userId);

        int requestedCount = 0;
        int syncedCount = 0;
        int geocodedCount = 0;
        int geocodingFailedCount = 0;
        int skippedCount = 0;
        String lastRateLimitLimit = null;
        String lastRateLimitUsage = null;

        try {
            String accessToken = tokenRefreshService.getValidAccessToken(userId);

            for (int page = 1; requestedCount < FIRST_SYNC_LIMIT; page++) {
                int remaining = FIRST_SYNC_LIMIT - requestedCount;
                int perPage = Math.min(PER_PAGE, remaining);

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
                }

                if (activities.size() < perPage) {
                    break;
                }
            }

            userRepository.updateLastSyncedAt(userId);
            String status = geocodingFailedCount > 0 ? "PARTIAL_SUCCESS" : "SUCCESS";
            syncLogRepository.finish(syncLog.getId(), status, requestedCount, syncedCount, skippedCount, null);

            return new SyncActivitiesResponse(
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
                    skippedCount,
                    exception.getMessage()
            );
            throw exception;
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
