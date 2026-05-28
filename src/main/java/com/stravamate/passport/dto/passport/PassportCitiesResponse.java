package com.stravamate.passport.dto.passport;

import java.util.List;

public record PassportCitiesResponse(
        List<PassportCityResponse> cities
) {
}
