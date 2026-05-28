package com.stravamate.passport.domain.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class ActivityPlace {

    private Long id;
    private Long activityId;
    private Long visitedPlaceId;
    private String source;
    private BigDecimal matchedLatitude;
    private BigDecimal matchedLongitude;
    private BigDecimal distanceMeters;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getVisitedPlaceId() {
        return visitedPlaceId;
    }

    public void setVisitedPlaceId(Long visitedPlaceId) {
        this.visitedPlaceId = visitedPlaceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getMatchedLatitude() {
        return matchedLatitude;
    }

    public void setMatchedLatitude(BigDecimal matchedLatitude) {
        this.matchedLatitude = matchedLatitude;
    }

    public BigDecimal getMatchedLongitude() {
        return matchedLongitude;
    }

    public void setMatchedLongitude(BigDecimal matchedLongitude) {
        this.matchedLongitude = matchedLongitude;
    }

    public BigDecimal getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(BigDecimal distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
