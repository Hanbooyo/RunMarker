package com.stravamate.passport.dto.passport;

import java.math.BigDecimal;
import java.time.Instant;

public record PassportCountryResponse(
        String countryCode,
        String countryName,
        Integer cityCount,
        Integer activityCount,
        BigDecimal totalDistanceMeters,
        BigDecimal totalDistanceKm,
        Instant firstActivityAt,
        Instant lastActivityAt,
        BigDecimal representativeLatitude,
        BigDecimal representativeLongitude
) {
}
