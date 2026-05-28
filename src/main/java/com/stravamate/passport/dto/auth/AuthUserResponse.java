package com.stravamate.passport.dto.auth;

import com.stravamate.passport.domain.entity.User;

import java.time.Instant;

public record AuthUserResponse(
        Long id,
        Long stravaAthleteId,
        String username,
        String firstname,
        String lastname,
        String profileImageUrl,
        Instant lastSyncedAt
) {

    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getStravaAthleteId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getProfileImageUrl(),
                user.getLastSyncedAt()
        );
    }
}
