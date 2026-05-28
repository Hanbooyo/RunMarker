package com.stravamate.passport.exception;

public class StravaApiException extends RuntimeException {

    public StravaApiException(String message) {
        super(message);
    }

    public StravaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
