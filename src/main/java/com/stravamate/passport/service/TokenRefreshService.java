package com.stravamate.passport.service;

import com.stravamate.passport.client.strava.StravaOAuthClient;
import com.stravamate.passport.domain.entity.StravaToken;
import com.stravamate.passport.dto.strava.StravaTokenResponse;
import com.stravamate.passport.exception.ResourceNotFoundException;
import com.stravamate.passport.exception.StravaApiException;
import com.stravamate.passport.repository.TokenRepository;
import com.stravamate.passport.security.TokenCipher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenRefreshService {

    private static final long REFRESH_MARGIN_SECONDS = 60;

    private final StravaOAuthClient stravaOAuthClient;
    private final TokenRepository tokenRepository;
    private final TokenCipher tokenCipher;

    public TokenRefreshService(
            StravaOAuthClient stravaOAuthClient,
            TokenRepository tokenRepository,
            TokenCipher tokenCipher
    ) {
        this.stravaOAuthClient = stravaOAuthClient;
        this.tokenRepository = tokenRepository;
        this.tokenCipher = tokenCipher;
    }

    @Transactional
    public String getValidAccessToken(Long userId) {
        StravaToken token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자의 Strava token을 찾을 수 없습니다."));

        if (!isExpiredOrNearExpiry(token)) {
            return tokenCipher.decrypt(token.getAccessToken());
        }

        String plainRefreshToken = tokenCipher.decrypt(token.getRefreshToken());
        StravaTokenResponse refreshed = stravaOAuthClient.refreshToken(plainRefreshToken);

        token.setAccessToken(tokenCipher.encrypt(refreshed.accessToken()));
        token.setRefreshToken(tokenCipher.encrypt(refreshed.refreshToken()));
        token.setExpiresAt(toExpiresAt(refreshed));

        StravaToken saved = tokenRepository.upsert(token);
        return tokenCipher.decrypt(saved.getAccessToken());
    }

    private boolean isExpiredOrNearExpiry(StravaToken token) {
        return token.getExpiresAt() == null
                || token.getExpiresAt().minusSeconds(REFRESH_MARGIN_SECONDS).isBefore(Instant.now());
    }

    private Instant toExpiresAt(StravaTokenResponse tokenResponse) {
        if (tokenResponse.expiresAt() == null) {
            throw new StravaApiException("Strava refresh token 응답에 만료 시각이 없습니다.");
        }
        return Instant.ofEpochSecond(tokenResponse.expiresAt());
    }
}
