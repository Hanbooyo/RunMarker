package com.stravamate.passport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String frontendBaseUrl,
        Cors cors,
        Auth auth,
        Security security,
        Strava strava,
        Geocoding geocoding
) {

    public record Cors(
            String allowedOrigins
    ) {
    }

    public record Auth(
            String loginSuccessPath,
            String loginErrorPath
    ) {
    }

    public record Security(
            Boolean devAuthEnabled,
            Boolean debugUserHeaderEnabled,
            String tokenCipher,
            String tokenEncryptionKey
    ) {
        public boolean isDevAuthEnabled() {
            return Boolean.TRUE.equals(devAuthEnabled);
        }

        public boolean isDebugUserHeaderEnabled() {
            return Boolean.TRUE.equals(debugUserHeaderEnabled);
        }
    }

    public record Strava(
            String clientId,
            String clientSecret,
            String redirectUri,
            String authorizeUrl,
            String tokenUrl,
            String apiBaseUrl
    ) {
    }

    public record Geocoding(
            String provider,
            String nominatimBaseUrl,
            String nominatimUserAgent,
            Integer cacheCoordinateScale
    ) {
    }
}
