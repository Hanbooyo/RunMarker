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

@RestController
@RequestMapping("/api/auth/strava")
public class AuthController {

    private final AuthService authService;
    private final AppProperties appProperties;

    public AuthController(AuthService authService, AppProperties appProperties) {
        this.authService = authService;
        this.appProperties = appProperties;
    }

    @GetMapping("/login")
    public RedirectView login() {
        return new RedirectView(authService.getStravaAuthorizationUrl());
    }

    @GetMapping("/callback")
    public RedirectView callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String error,
            HttpSession session
    ) {
        if (error != null) {
            return new RedirectView(buildFrontendRedirect(appProperties.auth().loginErrorPath(), "strava_denied"));
        }

        try {
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
}
