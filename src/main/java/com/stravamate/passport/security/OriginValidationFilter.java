package com.stravamate.passport.security;

import com.stravamate.passport.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class OriginValidationFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;

    public OriginValidationFilter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!isStateChangingApiRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestOrigin = resolveRequestOrigin(request);
        if (requestOrigin == null || allowedOrigins().contains(requestOrigin)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "허용되지 않은 요청 출처입니다.");
    }

    private boolean isStateChangingApiRequest(HttpServletRequest request) {
        String method = request.getMethod();
        return request.getRequestURI().startsWith("/api/")
                && ("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method));
    }

    private String resolveRequestOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            return normalizeOrigin(origin);
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return normalizeOrigin(referer);
        }

        return null;
    }

    private Set<String> allowedOrigins() {
        Set<String> origins = new HashSet<>();
        origins.add(normalizeOrigin(appProperties.frontendBaseUrl()));

        String configured = appProperties.cors() == null ? null : appProperties.cors().allowedOrigins();
        if (configured != null && !configured.isBlank()) {
            Arrays.stream(configured.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .map(this::normalizeOrigin)
                    .forEach(origins::add);
        }

        return origins;
    }

    private String normalizeOrigin(String value) {
        try {
            URI uri = URI.create(value);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return value;
            }

            int port = uri.getPort();
            String base = uri.getScheme() + "://" + uri.getHost();
            if (port != -1) {
                return base + ":" + port;
            }
            return base;
        } catch (IllegalArgumentException exception) {
            return value;
        }
    }
}
