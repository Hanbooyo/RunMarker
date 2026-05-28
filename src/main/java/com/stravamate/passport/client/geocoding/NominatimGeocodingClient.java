package com.stravamate.passport.client.geocoding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.dto.geocoding.GeocodingResult;
import com.stravamate.passport.dto.geocoding.NominatimReverseResponse;
import com.stravamate.passport.exception.GeocodingException;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

@Primary
@Component
public class NominatimGeocodingClient implements GeocodingClient {

    private static final long MIN_REQUEST_INTERVAL_MILLIS = 1_100L;

    private final AppProperties appProperties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private long lastRequestAtMillis = 0L;

    public NominatimGeocodingClient(
            AppProperties appProperties,
            RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.appProperties = appProperties;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<GeocodingResult> reverseGeocode(BigDecimal latitude, BigDecimal longitude) {
        throttle();

        try {
            NominatimReverseResponse response = restClient.get()
                    .uri(appProperties.geocoding().nominatimBaseUrl()
                                    + "/reverse?format=jsonv2&lat={lat}&lon={lon}&zoom=10&addressdetails=1",
                            latitude,
                            longitude)
                    .header(HttpHeaders.USER_AGENT, appProperties.geocoding().nominatimUserAgent())
                    .retrieve()
                    .body(NominatimReverseResponse.class);

            if (response == null || response.address() == null) {
                return Optional.empty();
            }

            return Optional.of(toResult(response));
        } catch (Exception exception) {
            throw new GeocodingException("Nominatim reverse geocoding 호출에 실패했습니다.", exception);
        }
    }

    @Override
    public String provider() {
        return "nominatim";
    }

    private synchronized void throttle() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRequestAtMillis;
        if (elapsed < MIN_REQUEST_INTERVAL_MILLIS) {
            try {
                Thread.sleep(MIN_REQUEST_INTERVAL_MILLIS - elapsed);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new GeocodingException("Geocoding 요청 대기 중 인터럽트가 발생했습니다.", exception);
            }
        }
        lastRequestAtMillis = System.currentTimeMillis();
    }

    private GeocodingResult toResult(NominatimReverseResponse response) {
        NominatimReverseResponse.Address address = response.address();
        return new GeocodingResult(
                resolveCity(address),
                resolveRegion(address),
                address.country(),
                normalizeCountryCode(address.countryCode()),
                toBigDecimal(response.lat()),
                toBigDecimal(response.lon()),
                toJson(response)
        );
    }

    private String resolveCity(NominatimReverseResponse.Address address) {
        if (StringUtils.hasText(address.city())) {
            return address.city();
        }
        if (StringUtils.hasText(address.town())) {
            return address.town();
        }
        if (StringUtils.hasText(address.village())) {
            return address.village();
        }
        if (StringUtils.hasText(address.municipality())) {
            return address.municipality();
        }
        return address.county();
    }

    private String resolveRegion(NominatimReverseResponse.Address address) {
        if (StringUtils.hasText(address.state())) {
            return address.state();
        }
        if (StringUtils.hasText(address.province())) {
            return address.province();
        }
        return address.region();
    }

    private String normalizeCountryCode(String countryCode) {
        return countryCode == null ? null : countryCode.toUpperCase();
    }

    private BigDecimal toBigDecimal(String value) {
        return StringUtils.hasText(value) ? new BigDecimal(value) : null;
    }

    private String toJson(NominatimReverseResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }
}
