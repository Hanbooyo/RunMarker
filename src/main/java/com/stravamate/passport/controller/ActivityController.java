package com.stravamate.passport.controller;

import com.stravamate.passport.dto.activity.ActivityListResponse;
import com.stravamate.passport.dto.activity.ActivityResponse;
import com.stravamate.passport.security.CurrentUserResolver;
import com.stravamate.passport.service.ActivityQueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityQueryService activityQueryService;
    private final CurrentUserResolver currentUserResolver;

    public ActivityController(
            ActivityQueryService activityQueryService,
            CurrentUserResolver currentUserResolver
    ) {
        this.activityQueryService = activityQueryService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ActivityListResponse getActivities(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return activityQueryService.getActivities(userId, page, size);
    }

    @GetMapping("/{id}")
    public ActivityResponse getActivity(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @PathVariable Long id
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return activityQueryService.getActivity(userId, id);
    }
}
