package com.stravamate.passport.domain.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class PassportCityStats {

    private Long visitedPlaceId;
    private String countryCode;
    private String countryName;
    private String regionName;
    private String cityName;
    private Integer activityCount;
    private BigDecimal totalDistanceMeters;
    private Instant firstActivityAt;
    private Instant lastActivityAt;
    private BigDecimal representativeLatitude;
    private BigDecimal representativeLongitude;

    public Long getVisitedPlaceId() {
        return visitedPlaceId;
    }

    public void setVisitedPlaceId(Long visitedPlaceId) {
        this.visitedPlaceId = visitedPlaceId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }

    public BigDecimal getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void setTotalDistanceMeters(BigDecimal totalDistanceMeters) {
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public Instant getFirstActivityAt() {
        return firstActivityAt;
    }

    public void setFirstActivityAt(Instant firstActivityAt) {
        this.firstActivityAt = firstActivityAt;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public BigDecimal getRepresentativeLatitude() {
        return representativeLatitude;
    }

    public void setRepresentativeLatitude(BigDecimal representativeLatitude) {
        this.representativeLatitude = representativeLatitude;
    }

    public BigDecimal getRepresentativeLongitude() {
        return representativeLongitude;
    }

    public void setRepresentativeLongitude(BigDecimal representativeLongitude) {
        this.representativeLongitude = representativeLongitude;
    }
}
