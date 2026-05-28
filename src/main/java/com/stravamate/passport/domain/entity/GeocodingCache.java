package com.stravamate.passport.domain.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class GeocodingCache {

    private Long id;
    private String cacheKey;
    private String provider;
    private BigDecimal roundedLatitude;
    private BigDecimal roundedLongitude;
    private String cityName;
    private String regionName;
    private String countryName;
    private String countryCode;
    private String rawResponse;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public BigDecimal getRoundedLatitude() {
        return roundedLatitude;
    }

    public void setRoundedLatitude(BigDecimal roundedLatitude) {
        this.roundedLatitude = roundedLatitude;
    }

    public BigDecimal getRoundedLongitude() {
        return roundedLongitude;
    }

    public void setRoundedLongitude(BigDecimal roundedLongitude) {
        this.roundedLongitude = roundedLongitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
