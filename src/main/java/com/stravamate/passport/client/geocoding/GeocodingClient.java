package com.stravamate.passport.client.geocoding;

import com.stravamate.passport.dto.geocoding.GeocodingResult;

import java.math.BigDecimal;
import java.util.Optional;

public interface GeocodingClient {

    Optional<GeocodingResult> reverseGeocode(BigDecimal latitude, BigDecimal longitude);

    String provider();
}
