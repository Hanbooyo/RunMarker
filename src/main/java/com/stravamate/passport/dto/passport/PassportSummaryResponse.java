package com.stravamate.passport.dto.passport;

import java.math.BigDecimal;
import java.util.List;

public record PassportSummaryResponse(
        int totalCountries,
        int totalCities,
        int totalActivities,
        BigDecimal totalDistanceMeters,
        BigDecimal totalDistanceKm,
        List<PassportCountryResponse> countries,
        List<PassportCityResponse> cities,
        List<PassportMapMarkerResponse> markers
) {
}
