package com.stravamate.passport.service;

import com.stravamate.passport.client.geocoding.GeocodingClient;
import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.domain.entity.Activity;
import com.stravamate.passport.domain.entity.ActivityPlace;
import com.stravamate.passport.domain.entity.GeocodingCache;
import com.stravamate.passport.domain.entity.VisitedPlace;
import com.stravamate.passport.dto.geocoding.GeocodingResult;
import com.stravamate.passport.repository.ActivityPlaceRepository;
import com.stravamate.passport.repository.ActivityRepository;
import com.stravamate.passport.repository.GeocodingCacheRepository;
import com.stravamate.passport.repository.VisitedPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class GeocodingService {

    private static final String SOURCE_START_LATLNG = "START_LATLNG";

    private final AppProperties appProperties;
    private final GeocodingClient geocodingClient;
    private final GeocodingCacheRepository geocodingCacheRepository;
    private final ActivityRepository activityRepository;
    private final VisitedPlaceRepository visitedPlaceRepository;
    private final ActivityPlaceRepository activityPlaceRepository;

    public GeocodingService(
            AppProperties appProperties,
            GeocodingClient geocodingClient,
            GeocodingCacheRepository geocodingCacheRepository,
            ActivityRepository activityRepository,
            VisitedPlaceRepository visitedPlaceRepository,
            ActivityPlaceRepository activityPlaceRepository
    ) {
        this.appProperties = appProperties;
        this.geocodingClient = geocodingClient;
        this.geocodingCacheRepository = geocodingCacheRepository;
        this.activityRepository = activityRepository;
        this.visitedPlaceRepository = visitedPlaceRepository;
        this.activityPlaceRepository = activityPlaceRepository;
    }

    @Transactional
    public boolean geocodeAndAttachPlace(Activity activity) {
        if (activity.getStartLatitude() == null || activity.getStartLongitude() == null) {
            return false;
        }

        Optional<GeocodingResult> result = resolveGeocoding(activity.getStartLatitude(), activity.getStartLongitude());
        if (result.isEmpty() || !result.get().hasRequiredPlace()) {
            return false;
        }

        GeocodingResult geocoding = result.get();

        activityRepository.updateGeocoding(
                activity.getId(),
                geocoding.cityName(),
                geocoding.regionName(),
                geocoding.countryName(),
                geocoding.countryCode()
        );

        VisitedPlace visitedPlace = visitedPlaceRepository.upsertIdentity(toVisitedPlace(activity, geocoding));
        Optional<ActivityPlace> insertedLink = activityPlaceRepository.insertIfNotExists(
                toActivityPlace(activity, visitedPlace.getId())
        );

        if (insertedLink.isPresent()) {
            visitedPlaceRepository.incrementStats(
                    visitedPlace.getId(),
                    activity.getStartDate(),
                    activity.getDistanceMeters() == null ? BigDecimal.ZERO : activity.getDistanceMeters()
            );
        }

        return true;
    }

    private Optional<GeocodingResult> resolveGeocoding(BigDecimal latitude, BigDecimal longitude) {
        BigDecimal roundedLatitude = roundCoordinate(latitude);
        BigDecimal roundedLongitude = roundCoordinate(longitude);
        String cacheKey = buildCacheKey(roundedLatitude, roundedLongitude);
        String provider = geocodingClient.provider();

        Optional<GeocodingCache> cached = geocodingCacheRepository.findByCacheKeyAndProvider(cacheKey, provider);
        if (cached.isPresent()) {
            return Optional.of(toResult(cached.get()));
        }

        Optional<GeocodingResult> geocoded = geocodingClient.reverseGeocode(latitude, longitude);
        geocoded
                .filter(GeocodingResult::hasRequiredPlace)
                .map(result -> toCache(cacheKey, provider, roundedLatitude, roundedLongitude, result))
                .ifPresent(geocodingCacheRepository::upsert);

        return geocoded;
    }

    private BigDecimal roundCoordinate(BigDecimal coordinate) {
        int scale = appProperties.geocoding().cacheCoordinateScale() == null
                ? 3
                : appProperties.geocoding().cacheCoordinateScale();

        return coordinate.setScale(scale, RoundingMode.HALF_UP);
    }

    private String buildCacheKey(BigDecimal roundedLatitude, BigDecimal roundedLongitude) {
        return roundedLatitude.toPlainString() + "," + roundedLongitude.toPlainString();
    }

    private GeocodingResult toResult(GeocodingCache cache) {
        return new GeocodingResult(
                cache.getCityName(),
                cache.getRegionName(),
                cache.getCountryName(),
                cache.getCountryCode(),
                cache.getRoundedLatitude(),
                cache.getRoundedLongitude(),
                cache.getRawResponse()
        );
    }

    private GeocodingCache toCache(
            String cacheKey,
            String provider,
            BigDecimal roundedLatitude,
            BigDecimal roundedLongitude,
            GeocodingResult result
    ) {
        GeocodingCache cache = new GeocodingCache();
        cache.setCacheKey(cacheKey);
        cache.setProvider(provider);
        cache.setRoundedLatitude(roundedLatitude);
        cache.setRoundedLongitude(roundedLongitude);
        cache.setCityName(normalizeRequired(result.cityName()));
        cache.setRegionName(normalizeNullable(result.regionName()));
        cache.setCountryName(normalizeRequired(result.countryName()));
        cache.setCountryCode(normalizeNullable(result.countryCode()));
        cache.setRawResponse(result.rawResponse());
        return cache;
    }

    private VisitedPlace toVisitedPlace(Activity activity, GeocodingResult result) {
        VisitedPlace visitedPlace = new VisitedPlace();
        visitedPlace.setUserId(activity.getUserId());
        visitedPlace.setCountryCode(normalizeNullable(result.countryCode()));
        visitedPlace.setCountryName(normalizeRequired(result.countryName()));
        visitedPlace.setRegionName(normalizeNullable(result.regionName()));
        visitedPlace.setCityName(normalizeRequired(result.cityName()));
        visitedPlace.setRepresentativeLatitude(activity.getStartLatitude());
        visitedPlace.setRepresentativeLongitude(activity.getStartLongitude());
        return visitedPlace;
    }

    private ActivityPlace toActivityPlace(Activity activity, Long visitedPlaceId) {
        ActivityPlace activityPlace = new ActivityPlace();
        activityPlace.setActivityId(activity.getId());
        activityPlace.setVisitedPlaceId(visitedPlaceId);
        activityPlace.setSource(SOURCE_START_LATLNG);
        activityPlace.setMatchedLatitude(activity.getStartLatitude());
        activityPlace.setMatchedLongitude(activity.getStartLongitude());
        activityPlace.setDistanceMeters(activity.getDistanceMeters());
        return activityPlace;
    }

    private String normalizeRequired(String value) {
        return value == null || value.isBlank() ? "Unknown" : value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null ? "" : value.trim();
    }
}
