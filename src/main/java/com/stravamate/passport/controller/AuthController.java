package com.stravamate.passport.controller;

import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.exception.AuthException;
import com.stravamate.passport.exception.StravaApiException;
import com.stravamate.passport.security.CurrentUserResolver;
import com.stravamate.passport.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth/strava")
public class AuthController {

    private static final String SESSION_OAUTH_STATE = "STRAVAMATE_OAUTH_STATE";

    private final AuthService authService;
    private final AppProperties appProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthController(AuthService authService, AppProperties appProperties) {
        this.authService = authService;
        this.appProperties = appProperties;
    }

    @GetMapping("/login")
    public RedirectView login(HttpSession session) {
        try {
            String state = createState();
            session.setAttribute(SESSION_OAUTH_STATE, state);
            return new RedirectView(authService.getStravaAuthorizationUrl(state));
        } catch (AuthException exception) {
            return new RedirectView(buildFrontendRedirect(appProperties.auth().loginErrorPath(), "missing_strava_config"));
        }
    }

    @GetMapping("/callback")
    public RedirectView callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String state,
            HttpSession session
    ) {
        if (error != null) {
            return new RedirectView(buildFrontendRedirect(appProperties.auth().loginErrorPath(), "strava_denied"));
        }

        try {
            validateState(session, state);
            var result = authService.handleCallback(code, scope);
            session.setAttribute(CurrentUserResolver.SESSION_USER_ID, result.userId());
            return new RedirectView(buildFrontendRedirect(appProperties.auth().loginSuccessPath(), null));
        } catch (AuthException | StravaApiException exception) {
            return new RedirectView(buildFrontendRedirect(appProperties.auth().loginErrorPath(), "auth_failed"));
        }
    }

    private String buildFrontendRedirect(String path, String errorCode) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(appProperties.frontendBaseUrl())
                .path(path);

        if (errorCode != null) {
            builder.queryParam("error", errorCode);
        }

        return builder.build().toUriString();
    }

    private String createState() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void validateState(HttpSession session, String state) {
        Object expectedState = session.getAttribute(SESSION_OAUTH_STATE);
        session.removeAttribute(SESSION_OAUTH_STATE);

        if (!(expectedState instanceof String expected) || state == null || !expected.equals(state)) {
            throw new AuthException("OAuth state 검증에 실패했습니다.");
        }
    }
}
