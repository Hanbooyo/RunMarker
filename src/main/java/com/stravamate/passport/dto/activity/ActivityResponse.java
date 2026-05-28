package com.stravamate.passport.dto.activity;

import com.stravamate.passport.domain.entity.Activity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        Long stravaActivityId,
        String name,
        String type,
        String sportType,
        Instant startDate,
        LocalDateTime startDateLocal,
        String timezone,
        BigDecimal distanceMeters,
        BigDecimal distanceKilometers,
        Integer movingTimeSeconds,
        Integer elapsedTimeSeconds,
        BigDecimal startLatitude,
        BigDecimal startLongitude,
        String cityName,
        String regionName,
        String countryName,
        String countryCode
) {

    public static ActivityResponse from(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getStravaActivityId(),
                activity.getName(),
                activity.getType(),
                activity.getSportType(),
                activity.getStartDate(),
                activity.getStartDateLocal(),
                activity.getTimezone(),
                activity.getDistanceMeters(),
                toKilometers(activity.getDistanceMeters()),
                activity.getMovingTimeSeconds(),
                activity.getElapsedTimeSeconds(),
                activity.getStartLatitude(),
                activity.getStartLongitude(),
                activity.getCityName(),
                activity.getRegionName(),
                activity.getCountryName(),
                activity.getCountryCode()
        );
    }

    private static BigDecimal toKilometers(BigDecimal meters) {
        if (meters == null) {
            return BigDecimal.ZERO;
        }
        return meters.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
    }
}
