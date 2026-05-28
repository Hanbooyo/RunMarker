package com.stravamate.passport.dto.passport;

import java.util.List;

public record PassportRecentPlacesResponse(
        List<PassportCityResponse> places
) {
}
