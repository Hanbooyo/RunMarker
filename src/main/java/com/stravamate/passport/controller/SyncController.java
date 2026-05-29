package com.stravamate.passport.controller;

import com.stravamate.passport.dto.sync.SyncActivitiesResponse;
import com.stravamate.passport.dto.sync.SyncJobStartResponse;
import com.stravamate.passport.dto.sync.SyncJobStatusResponse;
import com.stravamate.passport.security.CurrentUserResolver;
import com.stravamate.passport.service.ActivitySyncService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final ActivitySyncService activitySyncService;
    private final CurrentUserResolver currentUserResolver;

    public SyncController(
            ActivitySyncService activitySyncService,
            CurrentUserResolver currentUserResolver
    ) {
        this.activitySyncService = activitySyncService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/activities")
    public SyncActivitiesResponse syncActivities(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(defaultValue = "recent") String mode
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return activitySyncService.syncActivities(userId, mode);
    }

    @PostMapping("/activities/jobs")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SyncJobStartResponse startActivitiesSyncJob(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(defaultValue = "full") String mode
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return activitySyncService.startActivitiesSyncJob(userId, mode);
    }

    @GetMapping("/activities/jobs/{syncLogId}")
    public SyncJobStatusResponse getActivitiesSyncJob(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @PathVariable Long syncLogId
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return activitySyncService.getSyncJobStatus(userId, syncLogId);
    }
}
