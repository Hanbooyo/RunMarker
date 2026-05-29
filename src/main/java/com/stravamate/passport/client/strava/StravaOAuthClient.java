package com.stravamate.passport.client.strava;

import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.dto.strava.StravaTokenResponse;
import com.stravamate.passport.exception.StravaApiException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class StravaOAuthClient {

    private static final String RESPONSE_TYPE_CODE = "code";
    private static final String APPROVAL_PROMPT_AUTO = "auto";
    private static final String SCOPE = "read,activity:read_all";

    private final AppProperties appProperties;
    private final RestClient restClient;

    public StravaOAuthClient(AppProperties appProperties, RestClient restClient) {
        this.appProperties = appProperties;
        this.restClient = restClient;
    }

    public String buildAuthorizationUrl(String state) {
        AppProperties.Strava strava = appProperties.strava();

        return UriComponentsBuilder.fromUriString(strava.authorizeUrl())
                .queryParam("client_id", strava.clientId())
                .queryParam("redirect_uri", strava.redirectUri())
                .queryParam("response_type", RESPONSE_TYPE_CODE)
                .queryParam("approval_prompt", APPROVAL_PROMPT_AUTO)
                .queryParam("scope", SCOPE)
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    public StravaTokenResponse exchangeCode(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", appProperties.strava().clientId());
        form.add("client_secret", appProperties.strava().clientSecret());
        form.add("code", code);
        form.add("grant_type", "authorization_code");

        return postTokenRequest(form);
    }

    public StravaTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", appProperties.strava().clientId());
        form.add("client_secret", appProperties.strava().clientSecret());
        form.add("refresh_token", refreshToken);
        form.add("grant_type", "refresh_token");

        return postTokenRequest(form);
    }

    private StravaTokenResponse postTokenRequest(MultiValueMap<String, String> form) {
        try {
            StravaTokenResponse response = restClient.post()
                    .uri(appProperties.strava().tokenUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(StravaTokenResponse.class);

            if (response == null || response.accessToken() == null || response.refreshToken() == null) {
                throw new StravaApiException("Strava token 응답이 비어 있습니다.");
            }

            return response;
        } catch (StravaApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new StravaApiException("Strava token API 호출에 실패했습니다.", exception);
        }
    }
}
