package com.stravamate.passport.service;

import com.stravamate.passport.domain.entity.PassportCityStats;
import com.stravamate.passport.domain.entity.PassportCountryStats;
import com.stravamate.passport.dto.passport.PassportCitiesResponse;
import com.stravamate.passport.dto.passport.PassportCityResponse;
import com.stravamate.passport.dto.passport.PassportCountriesResponse;
import com.stravamate.passport.dto.passport.PassportCountryResponse;
import com.stravamate.passport.dto.passport.PassportMapMarkerResponse;
import com.stravamate.passport.dto.passport.PassportMapMarkersResponse;
import com.stravamate.passport.dto.passport.PassportRecentPlacesResponse;
import com.stravamate.passport.dto.passport.PassportSummaryResponse;
import com.stravamate.passport.repository.PassportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PassportService {

    private final PassportRepository passportRepository;

    public PassportService(PassportRepository passportRepository) {
        this.passportRepository = passportRepository;
    }

    public PassportSummaryResponse getSummary(Long userId) {
        List<PassportCountryResponse> countries = getCountryResponses(userId);
        List<PassportCityResponse> cities = getCityResponses(userId);
        List<PassportMapMarkerResponse> markers = getMarkerResponses(userId);

        BigDecimal totalDistanceMeters = countries.stream()
                .map(PassportCountryResponse::totalDistanceMeters)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalActivities = countries.stream()
                .mapToInt(country -> country.activityCount() == null ? 0 : country.activityCount())
                .sum();

        return new PassportSummaryResponse(
                countries.size(),
                cities.size(),
                totalActivities,
                totalDistanceMeters,
                toKilometers(totalDistanceMeters),
                countries,
                cities,
                markers
        );
    }

    public PassportCountriesResponse getCountries(Long userId) {
        return new PassportCountriesResponse(getCountryResponses(userId));
    }

    public PassportCitiesResponse getCities(Long userId) {
        return new PassportCitiesResponse(getCityResponses(userId));
    }

    public PassportMapMarkersResponse getMapMarkers(Long userId) {
        return new PassportMapMarkersResponse(getMarkerResponses(userId));
    }

    public PassportRecentPlacesResponse getRecentPlaces(Long userId, int limit) {
        int normalizedLimit = Math.min(Math.max(limit, 1), 50);
        List<PassportCityResponse> places = passportRepository.findRecentPlaces(userId, normalizedLimit)
                .stream()
                .map(this::toCityResponse)
                .toList();

        return new PassportRecentPlacesResponse(places);
    }

    private List<PassportCountryResponse> getCountryResponses(Long userId) {
        return passportRepository.findCountryStats(userId)
                .stream()
                .map(this::toCountryResponse)
                .toList();
    }

    private List<PassportCityResponse> getCityResponses(Long userId) {
        return passportRepository.findCityStats(userId)
                .stream()
                .map(this::toCityResponse)
                .toList();
    }

    private List<PassportMapMarkerResponse> getMarkerResponses(Long userId) {
        return passportRepository.findMapMarkerStats(userId)
                .stream()
                .map(this::toMarkerResponse)
                .toList();
    }

    private PassportCountryResponse toCountryResponse(PassportCountryStats stats) {
        BigDecimal meters = defaultZero(stats.getTotalDistanceMeters());
        return new PassportCountryResponse(
                stats.getCountryCode(),
                stats.getCountryName(),
                defaultZero(stats.getCityCount()),
                defaultZero(stats.getActivityCount()),
                meters,
                toKilometers(meters),
                stats.getFirstActivityAt(),
                stats.getLastActivityAt(),
                stats.getRepresentativeLatitude(),
                stats.getRepresentativeLongitude()
        );
    }

    private PassportCityResponse toCityResponse(PassportCityStats stats) {
        BigDecimal meters = defaultZero(stats.getTotalDistanceMeters());
        return new PassportCityResponse(
                stats.getVisitedPlaceId(),
                stats.getCityName(),
                stats.getRegionName(),
                stats.getCountryName(),
                stats.getCountryCode(),
                defaultZero(stats.getActivityCount()),
                meters,
                toKilometers(meters),
                stats.getFirstActivityAt(),
                stats.getLastActivityAt(),
                stats.getRepresentativeLatitude(),
                stats.getRepresentativeLongitude()
        );
    }

    private PassportMapMarkerResponse toMarkerResponse(PassportCityStats stats) {
        return new PassportMapMarkerResponse(
                stats.getVisitedPlaceId(),
                buildMarkerLabel(stats),
                stats.getCityName(),
                stats.getRegionName(),
                stats.getCountryName(),
                stats.getCountryCode(),
                stats.getRepresentativeLatitude(),
                stats.getRepresentativeLongitude(),
                defaultZero(stats.getActivityCount()),
                toKilometers(defaultZero(stats.getTotalDistanceMeters())),
                stats.getFirstActivityAt(),
                stats.getLastActivityAt()
        );
    }

    private String buildMarkerLabel(PassportCityStats stats) {
        if (stats.getCountryName() == null || stats.getCountryName().isBlank()) {
            return stats.getCityName();
        }
        return stats.getCityName() + ", " + stats.getCountryName();
    }

    private BigDecimal toKilometers(BigDecimal meters) {
        return defaultZero(meters).divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int defaultZero(Integer value) {
        return value == null ? 0 : value;
    }
}
