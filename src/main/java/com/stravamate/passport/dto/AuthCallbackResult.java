package com.stravamate.passport.dto;

public record AuthCallbackResult(
        Long userId,
        Long stravaAthleteId
) {
}
