package com.stravamate.passport.dto.auth;

public record SessionStatusResponse(
        boolean authenticated,
        int maxInactiveIntervalSeconds,
        long remainingSeconds,
        long expiresAtEpochMillis
) {
}
