package com.stravamate.passport.domain.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class VisitedPlace {

    private Long id;
    private Long userId;
    private String countryCode;
    private String countryName;
    private String regionName;
    private String cityName;
    private BigDecimal representativeLatitude;
    private BigDecimal representativeLongitude;
    private Instant firstActivityAt;
    private Instant lastActivityAt;
    private Integer activityCount;
    private BigDecimal totalDistanceMeters;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
