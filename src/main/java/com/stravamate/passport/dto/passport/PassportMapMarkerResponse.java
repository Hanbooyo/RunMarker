package com.stravamate.passport.dto.passport;

import java.math.BigDecimal;
import java.time.Instant;

public record PassportMapMarkerResponse(
        Long visitedPlaceId,
        String label,
        String cityName,
        String regionName,
        String countryName,
        String countryCode,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer activityCount,
        BigDecimal totalDistanceKm,
        Instant firstActivityAt,
        Instant lastActivityAt
) {
}
