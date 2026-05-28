package com.stravamate.passport.service;

import com.stravamate.passport.domain.entity.PassportCityStats;
import com.stravamate.passport.domain.entity.PassportCountryStats;
import com.stravamate.passport.dto.passport.PassportSummaryResponse;
import com.stravamate.passport.repository.PassportRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PassportServiceTest {

    @Test
    void getSummaryAggregatesTotalsForFrontend() {
        PassportRepository repository = mock(PassportRepository.class);
        PassportService service = new PassportService(repository);

        PassportCountryStats korea = country("KR", "South Korea", 2, 3, "21000.00");
        PassportCountryStats japan = country("JP", "Japan", 1, 1, "5000.00");
        PassportCityStats seoul = city(1L, "Seoul", "South Korea", 2, "15000.00");
        PassportCityStats busan = city(2L, "Busan", "South Korea", 1, "6000.00");

        when(repository.findCountryStats(1L)).thenReturn(List.of(korea, japan));
        when(repository.findCityStats(1L)).thenReturn(List.of(seoul, busan));
        when(repository.findMapMarkerStats(1L)).thenReturn(List.of(seoul, busan));

        PassportSummaryResponse response = service.getSummary(1L);

        assertThat(response.totalCountries()).isEqualTo(2);
        assertThat(response.totalCities()).isEqualTo(2);
        assertThat(response.totalActivities()).isEqualTo(4);
        assertThat(response.totalDistanceMeters()).isEqualByComparingTo("26000.00");
        assertThat(response.totalDistanceKm()).isEqualByComparingTo("26.00");
        assertThat(response.countries()).hasSize(2);
        assertThat(response.cities()).hasSize(2);
        assertThat(response.markers()).hasSize(2);
    }

    private PassportCountryStats country(
            String countryCode,
            String countryName,
            int cityCount,
            int activityCount,
            String distanceMeters
    ) {
        PassportCountryStats stats = new PassportCountryStats();
        stats.setCountryCode(countryCode);
        stats.setCountryName(countryName);
        stats.setCityCount(cityCount);
        stats.setActivityCount(activityCount);
        stats.setTotalDistanceMeters(new BigDecimal(distanceMeters));
        stats.setFirstActivityAt(Instant.parse("2026-01-01T00:00:00Z"));
        stats.setLastActivityAt(Instant.parse("2026-02-01T00:00:00Z"));
        return stats;
    }

    private PassportCityStats city(Long id, String cityName, String countryName, int activityCount, String distanceMeters) {
        PassportCityStats stats = new PassportCityStats();
        stats.setVisitedPlaceId(id);
        stats.setCityName(cityName);
        stats.setCountryName(countryName);
        stats.setCountryCode(countryName.substring(0, 2).toUpperCase());
        stats.setActivityCount(activityCount);
        stats.setTotalDistanceMeters(new BigDecimal(distanceMeters));
        stats.setFirstActivityAt(Instant.parse("2026-01-01T00:00:00Z"));
        stats.setLastActivityAt(Instant.parse("2026-02-01T00:00:00Z"));
        stats.setRepresentativeLatitude(new BigDecimal("37.5665"));
        stats.setRepresentativeLongitude(new BigDecimal("126.9780"));
        return stats;
    }
}
