package com.stravamate.passport.security;

import com.stravamate.passport.exception.AuthException;
import com.stravamate.passport.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    public static final String SESSION_USER_ID = "STRAVAMATE_USER_ID";

    private final UserRepository userRepository;

    public CurrentUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long resolveUserId(HttpServletRequest request, String userIdHeader) {
        Long sessionUserId = resolveFromSession(request);
        if (sessionUserId != null) {
            assertUserExists(sessionUserId);
            return sessionUserId;
        }

        return resolveFromHeader(userIdHeader);
    }

    private Long resolveFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(SESSION_USER_ID);
        if (value instanceof Long userId) {
            return userId;
        }
        if (value instanceof String userId) {
            try {
                return Long.valueOf(userId);
            } catch (NumberFormatException exception) {
                throw new AuthException("세션 사용자 ID 형식이 올바르지 않습니다.", exception);
            }
        }
        return null;
    }

    private Long resolveFromHeader(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new AuthException("로그인 세션이 없거나 X-User-Id 헤더가 없습니다.");
        }

        try {
            Long userId = Long.valueOf(userIdHeader);
            assertUserExists(userId);
            return userId;
        } catch (NumberFormatException exception) {
            throw new AuthException("X-User-Id 헤더는 숫자여야 합니다.", exception);
        }
    }

    private void assertUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("인증된 사용자를 찾을 수 없습니다."));
    }
}
