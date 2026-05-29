package com.stravamate.passport.controller;

import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.dto.auth.AuthUserResponse;
import com.stravamate.passport.dto.AuthCallbackResult;
import com.stravamate.passport.dto.auth.SessionStatusResponse;
import com.stravamate.passport.exception.AuthException;
import com.stravamate.passport.security.CurrentUserResolver;
import com.stravamate.passport.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthSessionController {

    private final AuthService authService;
    private final CurrentUserResolver currentUserResolver;
    private final AppProperties appProperties;

    public AuthSessionController(AuthService authService, CurrentUserResolver currentUserResolver, AppProperties appProperties) {
        this.authService = authService;
        this.currentUserResolver = currentUserResolver;
        this.appProperties = appProperties;
    }

    @GetMapping("/me")
    public AuthUserResponse me(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return authService.getCurrentUser(userId);
    }

    @GetMapping("/session")
    public SessionStatusResponse session(HttpServletRequest request) {
        return toSessionStatus(request.getSession(false));
    }

    @PostMapping("/session/refresh")
    public SessionStatusResponse refreshSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(CurrentUserResolver.SESSION_USER_ID) == null) {
            return toSessionStatus(null);
        }

        session.setAttribute("STRAVAMATE_SESSION_REFRESHED_AT", System.currentTimeMillis());
        return toFreshSessionStatus(session);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @PostMapping("/dev/login")
    public AuthUserResponse devLogin(HttpServletRequest request) {
        if (appProperties.security() == null || !appProperties.security().isDevAuthEnabled()) {
            throw new AuthException("개발용 로그인은 현재 환경에서 비활성화되어 있습니다.");
        }

        AuthCallbackResult result = authService.loginDevelopmentUser();
        request.getSession(true).setAttribute(CurrentUserResolver.SESSION_USER_ID, result.userId());
        return authService.getCurrentUser(result.userId());
    }

    private SessionStatusResponse toSessionStatus(HttpSession session) {
        if (session == null || session.getAttribute(CurrentUserResolver.SESSION_USER_ID) == null) {
            return new SessionStatusResponse(false, 0, 0, 0);
        }

        int maxInactiveIntervalSeconds = session.getMaxInactiveInterval();
        long elapsedSeconds = Math.max(0, (System.currentTimeMillis() - session.getLastAccessedTime()) / 1000);
        long remainingSeconds = Math.max(0, maxInactiveIntervalSeconds - elapsedSeconds);
        long expiresAtEpochMillis = System.currentTimeMillis() + remainingSeconds * 1000;

        return new SessionStatusResponse(true, maxInactiveIntervalSeconds, remainingSeconds, expiresAtEpochMillis);
    }

    private SessionStatusResponse toFreshSessionStatus(HttpSession session) {
        int maxInactiveIntervalSeconds = session.getMaxInactiveInterval();
        long expiresAtEpochMillis = System.currentTimeMillis() + maxInactiveIntervalSeconds * 1000L;
        return new SessionStatusResponse(true, maxInactiveIntervalSeconds, maxInactiveIntervalSeconds, expiresAtEpochMillis);
    }
}
