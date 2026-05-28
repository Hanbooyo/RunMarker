package com.stravamate.passport.dto.passport;

import java.math.BigDecimal;
import java.time.Instant;

public record PassportCityResponse(
        Long visitedPlaceId,
        String cityName,
        String regionName,
        String countryName,
        String countryCode,
        Integer activityCount,
        BigDecimal totalDistanceMeters,
        BigDecimal totalDistanceKm,
        Instant firstActivityAt,
        Instant lastActivityAt,
        BigDecimal representativeLatitude,
        BigDecimal representativeLongitude
) {
}
