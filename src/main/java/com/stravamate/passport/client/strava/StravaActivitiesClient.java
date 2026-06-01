package com.stravamate.passport.client.strava;

import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.dto.strava.StravaActivityResponse;
import com.stravamate.passport.exception.StravaApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class StravaActivitiesClient {

    private final AppProperties appProperties;
    private final RestClient restClient;

    public StravaActivitiesClient(AppProperties appProperties, RestClient restClient) {
        this.appProperties = appProperties;
        this.restClient = restClient;
    }

    public StravaActivitiesPage getAthleteActivities(String accessToken, int page, int perPage) {
        try {
            ResponseEntity<List<StravaActivityResponse>> response = restClient.get()
                    .uri(appProperties.strava().apiBaseUrl() + "/athlete/activities?page={page}&per_page={perPage}",
                            page,
                            perPage)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            List<StravaActivityResponse> activities = response.getBody() == null
                    ? List.of()
                    : response.getBody();

            return new StravaActivitiesPage(
                    activities,
                    response.getHeaders().getFirst("X-RateLimit-Limit"),
                    response.getHeaders().getFirst("X-RateLimit-Usage")
            );
        } catch (Exception exception) {
            throw new StravaApiException("활동 목록 조회에 실패했습니다.", exception);
        }
    }

    public record StravaActivitiesPage(
            List<StravaActivityResponse> activities,
            String rateLimitLimit,
            String rateLimitUsage
    ) {
    }
}
