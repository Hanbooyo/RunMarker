package com.stravamate.passport.dto.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StravaAthleteResponse(
        Long id,
        String username,
        String firstname,
        String lastname,
        String profile,
        @JsonProperty("profile_medium")
        String profileMedium
) {
}
