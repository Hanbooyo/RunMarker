package com.stravamate.passport.service;

import com.stravamate.passport.client.strava.StravaOAuthClient;
import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.domain.entity.StravaToken;
import com.stravamate.passport.domain.entity.User;
import com.stravamate.passport.dto.AuthCallbackResult;
import com.stravamate.passport.dto.auth.AuthUserResponse;
import com.stravamate.passport.dto.strava.StravaAthleteResponse;
import com.stravamate.passport.dto.strava.StravaTokenResponse;
import com.stravamate.passport.exception.AuthException;
import com.stravamate.passport.exception.ResourceNotFoundException;
import com.stravamate.passport.repository.TokenRepository;
import com.stravamate.passport.repository.UserRepository;
import com.stravamate.passport.security.TokenCipher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
public class AuthService {

    private static final long DEVELOPMENT_ATHLETE_ID = -1L;

    private final AppProperties appProperties;
    private final StravaOAuthClient stravaOAuthClient;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenCipher tokenCipher;

    public AuthService(
            AppProperties appProperties,
            StravaOAuthClient stravaOAuthClient,
            UserRepository userRepository,
            TokenRepository tokenRepository,
            TokenCipher tokenCipher
    ) {
        this.appProperties = appProperties;
        this.stravaOAuthClient = stravaOAuthClient;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenCipher = tokenCipher;
    }

    public String getStravaAuthorizationUrl(String state) {
        validateStravaClientConfig();
        return stravaOAuthClient.buildAuthorizationUrl(state);
    }

    public AuthUserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        return AuthUserResponse.from(user);
    }

    @Transactional
    public AuthCallbackResult loginDevelopmentUser() {
        User user = new User();
        user.setStravaAthleteId(DEVELOPMENT_ATHLETE_ID);
        user.setUsername("local-dev");
        user.setFirstname("Local");
        user.setLastname("Runner");
        user.setProfileImageUrl(null);

        User savedUser = userRepository.upsert(user);
        return new AuthCallbackResult(savedUser.getId(), savedUser.getStravaAthleteId());
    }

    @Transactional
    public AuthCallbackResult handleCallback(String code, String scope) {
        if (!StringUtils.hasText(code)) {
            throw new AuthException("Strava authorization code가 없습니다.");
        }

        validateStravaClientConfig();

        StravaTokenResponse tokenResponse = stravaOAuthClient.exchangeCode(code);
        StravaAthleteResponse athlete = tokenResponse.athlete();

        if (athlete == null || athlete.id() == null) {
            throw new AuthException("Strava athlete 정보가 없습니다.");
        }

        User user = userRepository.upsert(toUser(athlete));
        tokenRepository.upsert(toToken(user.getId(), tokenResponse, scope));

        return new AuthCallbackResult(user.getId(), user.getStravaAthleteId());
    }

    private User toUser(StravaAthleteResponse athlete) {
        User user = new User();
        user.setStravaAthleteId(athlete.id());
        user.setUsername(athlete.username());
        user.setFirstname(athlete.firstname());
        user.setLastname(athlete.lastname());
        user.setProfileImageUrl(resolveProfileImageUrl(athlete));
        return user;
    }

    private String resolveProfileImageUrl(StravaAthleteResponse athlete) {
        if (StringUtils.hasText(athlete.profile())) {
            return athlete.profile();
        }
        return athlete.profileMedium();
    }

    private StravaToken toToken(Long userId, StravaTokenResponse tokenResponse, String scope) {
        StravaToken token = new StravaToken();
        token.setUserId(userId);
        token.setAccessToken(tokenCipher.encrypt(tokenResponse.accessToken()));
        token.setRefreshToken(tokenCipher.encrypt(tokenResponse.refreshToken()));
        token.setExpiresAt(toExpiresAt(tokenResponse));
        token.setScope(scope);
        return token;
    }

    private Instant toExpiresAt(StravaTokenResponse tokenResponse) {
        if (tokenResponse.expiresAt() == null) {
            throw new AuthException("Strava token 만료 시각이 없습니다.");
        }
        return Instant.ofEpochSecond(tokenResponse.expiresAt());
    }

    private void validateStravaClientConfig() {
        if (!isUsableSecret(appProperties.strava().clientId())
                || !isUsableSecret(appProperties.strava().clientSecret())) {
            throw new AuthException("Strava client 설정이 없습니다. STRAVA_CLIENT_ID, STRAVA_CLIENT_SECRET 환경변수를 확인하세요.");
        }
    }
    private boolean isUsableSecret(String value) {
        return StringUtils.hasText(value) && !value.startsWith("your-");
    }
}
