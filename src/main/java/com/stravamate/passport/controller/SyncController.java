package com.stravamate.passport.controller;

import com.stravamate.passport.dto.sync.SyncActivitiesResponse;
import com.stravamate.passport.security.CurrentUserResolver;
import com.stravamate.passport.service.ActivitySyncService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return activitySyncService.syncRecentActivities(userId);
    }
}
