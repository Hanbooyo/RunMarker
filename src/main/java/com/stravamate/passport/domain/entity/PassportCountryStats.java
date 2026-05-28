package com.stravamate.passport.domain.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class PassportCountryStats {

    private String countryCode;
    private String countryName;
    private Integer cityCount;
    private Integer activityCount;
    private BigDecimal totalDistanceMeters;
    private Instant firstActivityAt;
    private Instant lastActivityAt;
    private BigDecimal representativeLatitude;
    private BigDecimal representativeLongitude;

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

    public Integer getCityCount() {
        return cityCount;
    }

    public void setCityCount(Integer cityCount) {
        this.cityCount = cityCount;
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
