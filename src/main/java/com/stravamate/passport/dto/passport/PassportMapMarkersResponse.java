package com.stravamate.passport.dto.passport;

import java.util.List;

public record PassportMapMarkersResponse(
        List<PassportMapMarkerResponse> markers
) {
}
