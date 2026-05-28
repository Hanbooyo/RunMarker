package com.stravamate.passport.dto.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NominatimReverseResponse(
        String lat,
        String lon,
        Address address
) {

    public record Address(
            String city,
            String town,
            String village,
            String municipality,
            String county,
            String state,
            String province,
            String region,
            String country,
            @JsonProperty("country_code")
            String countryCode
    ) {
    }
}
