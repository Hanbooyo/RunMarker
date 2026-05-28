package com.stravamate.passport.service;

import com.stravamate.passport.domain.entity.Activity;
import com.stravamate.passport.dto.activity.ActivityListResponse;
import com.stravamate.passport.dto.activity.ActivityResponse;
import com.stravamate.passport.exception.ResourceNotFoundException;
import com.stravamate.passport.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityQueryService {

    private final ActivityRepository activityRepository;

    public ActivityQueryService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityListResponse getActivities(Long userId, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int offset = normalizedPage * normalizedSize;

        List<ActivityResponse> activities = activityRepository.findAllByUserId(userId, normalizedSize, offset)
                .stream()
                .map(ActivityResponse::from)
                .toList();

        return new ActivityListResponse(activities, normalizedPage, normalizedSize);
    }

    public ActivityResponse getActivity(Long userId, Long id) {
        Activity activity = activityRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));

        return ActivityResponse.from(activity);
    }
}
