package com.stravamate.passport.controller;

import com.stravamate.passport.dto.passport.PassportCitiesResponse;
import com.stravamate.passport.dto.passport.PassportCountriesResponse;
import com.stravamate.passport.dto.passport.PassportMapMarkersResponse;
import com.stravamate.passport.dto.passport.PassportRecentPlacesResponse;
import com.stravamate.passport.dto.passport.PassportSummaryResponse;
import com.stravamate.passport.security.CurrentUserResolver;
import com.stravamate.passport.service.PassportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passport")
public class PassportController {

    private final PassportService passportService;
    private final CurrentUserResolver currentUserResolver;

    public PassportController(
            PassportService passportService,
            CurrentUserResolver currentUserResolver
    ) {
        this.passportService = passportService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/summary")
    public PassportSummaryResponse getSummary(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return passportService.getSummary(userId);
    }

    @GetMapping("/countries")
    public PassportCountriesResponse getCountries(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return passportService.getCountries(userId);
    }

    @GetMapping("/cities")
    public PassportCitiesResponse getCities(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return passportService.getCities(userId);
    }

    @GetMapping("/map-markers")
    public PassportMapMarkersResponse getMapMarkers(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return passportService.getMapMarkers(userId);
    }

    @GetMapping("/recent-places")
    public PassportRecentPlacesResponse getRecentPlaces(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        return passportService.getRecentPlaces(userId, limit);
    }
}
