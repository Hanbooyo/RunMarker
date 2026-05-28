package com.stravamate.passport.dto.passport;

import java.util.List;

public record PassportCountriesResponse(
        List<PassportCountryResponse> countries
) {
}
