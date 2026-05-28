package com.stravamate.passport.dto.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record StravaActivityResponse(
        Long id,
        String name,
        String type,
        @JsonProperty("sport_type")
        String sportType,
        BigDecimal distance,
        @JsonProperty("moving_time")
        Integer movingTime,
        @JsonProperty("elapsed_time")
        Integer elapsedTime,
        @JsonProperty("start_date")
        Instant startDate,
        @JsonProperty("start_date_local")
        String startDateLocal,
        String timezone,
        @JsonProperty("start_latlng")
        List<BigDecimal> startLatlng
) {

    public boolean isRun() {
        return "Run".equals(type) || "Run".equals(sportType);
    }

    public boolean hasStartLatlng() {
        return startLatlng != null
                && startLatlng.size() >= 2
                && startLatlng.get(0) != null
                && startLatlng.get(1) != null;
    }

    public BigDecimal startLatitude() {
        return startLatlng.get(0);
    }

    public BigDecimal startLongitude() {
        return startLatlng.get(1);
    }

    public LocalDateTime parsedStartDateLocal() {
        if (startDateLocal == null || startDateLocal.isBlank()) {
            return null;
        }

        if (startDateLocal.endsWith("Z") || startDateLocal.matches(".*[+-]\\d{2}:\\d{2}$")) {
            return OffsetDateTime.parse(startDateLocal, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toLocalDateTime();
        }

        return LocalDateTime.parse(startDateLocal, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
