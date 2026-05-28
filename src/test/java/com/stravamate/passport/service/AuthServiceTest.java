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
import com.stravamate.passport.repository.TokenRepository;
import com.stravamate.passport.repository.UserRepository;
import com.stravamate.passport.security.TokenCipher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private StravaOAuthClient stravaOAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenCipher tokenCipher;

    @Test
    void handleCallbackStoresUserAndToken() {
        AuthService authService = new AuthService(
                appProperties(),
                stravaOAuthClient,
                userRepository,
                tokenRepository,
                tokenCipher
        );

        StravaAthleteResponse athlete = new StravaAthleteResponse(
                987L,
                "runner",
                "Jane",
                "Runner",
                "https://example.com/profile.jpg",
                "https://example.com/profile-medium.jpg"
        );
        StravaTokenResponse tokenResponse = new StravaTokenResponse(
                "Bearer",
                "plain-access",
                "plain-refresh",
                1_800_000_000L,
                3600L,
                athlete
        );
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setStravaAthleteId(987L);

        when(stravaOAuthClient.exchangeCode("code-123")).thenReturn(tokenResponse);
        when(userRepository.upsert(any(User.class))).thenReturn(savedUser);
        when(tokenCipher.encrypt("plain-access")).thenReturn("encrypted-access");
        when(tokenCipher.encrypt("plain-refresh")).thenReturn("encrypted-refresh");
        when(tokenRepository.upsert(any(StravaToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthCallbackResult result = authService.handleCallback("code-123", "read,activity:read_all");

        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.stravaAthleteId()).isEqualTo(987L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).upsert(userCaptor.capture());
        assertThat(userCaptor.getValue().getFirstname()).isEqualTo("Jane");
        assertThat(userCaptor.getValue().getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");

        ArgumentCaptor<StravaToken> tokenCaptor = ArgumentCaptor.forClass(StravaToken.class);
        verify(tokenRepository).upsert(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getAccessToken()).isEqualTo("encrypted-access");
        assertThat(tokenCaptor.getValue().getRefreshToken()).isEqualTo("encrypted-refresh");
        assertThat(tokenCaptor.getValue().getExpiresAt()).isEqualTo(Instant.ofEpochSecond(1_800_000_000L));
    }

    @Test
    void handleCallbackRejectsEmptyCode() {
        AuthService authService = new AuthService(
                appProperties(),
                stravaOAuthClient,
                userRepository,
                tokenRepository,
                tokenCipher
        );

        assertThatThrownBy(() -> authService.handleCallback("", "read"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("authorization code");
    }

    @Test
    void getCurrentUserReturnsUserResponse() {
        AuthService authService = new AuthService(
                appProperties(),
                stravaOAuthClient,
                userRepository,
                tokenRepository,
                tokenCipher
        );
        User user = new User();
        user.setId(1L);
        user.setStravaAthleteId(987L);
        user.setFirstname("Jane");
        user.setLastname("Runner");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AuthUserResponse response = authService.getCurrentUser(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.stravaAthleteId()).isEqualTo(987L);
        assertThat(response.firstname()).isEqualTo("Jane");
    }

    private AppProperties appProperties() {
        return new AppProperties(
                "http://localhost:5173",
                new AppProperties.Cors("http://localhost:5173"),
                new AppProperties.Auth("/login/success", "/login/error"),
                new AppProperties.Strava(
                        "client-id",
                        "client-secret",
                        "http://localhost:8080/api/auth/strava/callback",
                        "https://www.strava.com/oauth/authorize",
                        "https://www.strava.com/oauth/token",
                        "https://www.strava.com/api/v3"
                ),
                new AppProperties.Geocoding("nominatim", "https://nominatim.openstreetmap.org", "test", 3)
        );
    }
}
