package com.stravamate.passport.dto.geocoding;

import java.math.BigDecimal;

public record GeocodingResult(
        String cityName,
        String regionName,
        String countryName,
        String countryCode,
        BigDecimal latitude,
        BigDecimal longitude,
        String rawResponse
) {

    public boolean hasRequiredPlace() {
        return countryName != null && !countryName.isBlank()
                && cityName != null && !cityName.isBlank();
    }
}
