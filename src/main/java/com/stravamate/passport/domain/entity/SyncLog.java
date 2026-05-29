package com.stravamate.passport.domain.entity;

import java.time.Instant;

public class SyncLog {

    private Long id;
    private Long userId;
    private String syncType;
    private String mode;
    private String status;
    private Integer requestedCount;
    private Integer syncedCount;
    private Integer geocodedCount;
    private Integer geocodingFailedCount;
    private Integer skippedCount;
    private String rateLimitLimit;
    private String rateLimitUsage;
    private String errorMessage;
    private Instant startedAt;
    private Instant finishedAt;

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

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(Integer requestedCount) {
        this.requestedCount = requestedCount;
    }

    public Integer getSyncedCount() {
        return syncedCount;
    }

    public void setSyncedCount(Integer syncedCount) {
        this.syncedCount = syncedCount;
    }

    public Integer getGeocodedCount() {
        return geocodedCount;
    }

    public void setGeocodedCount(Integer geocodedCount) {
        this.geocodedCount = geocodedCount;
    }

    public Integer getGeocodingFailedCount() {
        return geocodingFailedCount;
    }

    public void setGeocodingFailedCount(Integer geocodingFailedCount) {
        this.geocodingFailedCount = geocodingFailedCount;
    }

    public Integer getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public String getRateLimitLimit() {
        return rateLimitLimit;
    }

    public void setRateLimitLimit(String rateLimitLimit) {
        this.rateLimitLimit = rateLimitLimit;
    }

    public String getRateLimitUsage() {
        return rateLimitUsage;
    }

    public void setRateLimitUsage(String rateLimitUsage) {
        this.rateLimitUsage = rateLimitUsage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }
}
