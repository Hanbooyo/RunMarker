package com.stravamate.passport.service;

import com.stravamate.passport.dto.HealthResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HealthService {

    public HealthResponse getHealth() {
        return new HealthResponse("UP", "runmarker-api", Instant.now());
    }
}
