package com.stravamate.passport.controller;

import com.stravamate.passport.dto.auth.AuthUserResponse;
import com.stravamate.passport.dto.AuthCallbackResult;
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

    public AuthSessionController(AuthService authService, CurrentUserResolver currentUserResolver) {
        this.authService = authService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/me")
    public AuthUserResponse me(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return authService.getCurrentUser(userId);
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
        AuthCallbackResult result = authService.loginDevelopmentUser();
        request.getSession(true).setAttribute(CurrentUserResolver.SESSION_USER_ID, result.userId());
        return authService.getCurrentUser(result.userId());
    }
}
