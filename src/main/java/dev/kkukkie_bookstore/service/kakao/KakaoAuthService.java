package dev.kkukkie_bookstore.service.kakao;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
public class KakaoAuthService extends HttpCallService{

    private final String baseUrl;
    private final String appKey;
    private final String redirectUri;
    private final String clientSecret;

    private String authToken;

    public KakaoAuthService(Environment environment) {
        this.baseUrl = environment.getProperty("kakao.baseUrl");
        this.appKey = environment.getProperty("kakao.appKey");
        this.redirectUri = environment.getProperty("kakao.redirectUri");
        this.clientSecret = environment.getProperty("kakao.clientSecret");
    }

    public boolean getKakaoAuthToken(String code) throws JSONException {
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", APP_TYPE_URL_ENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", appKey);
        parameters.add("redirect_uri", redirectUri);
        parameters.add("client_secret", clientSecret);

        HttpEntity<?> requestEntity = httpClientEntity(header, parameters);

        ResponseEntity<String> response = httpRequest(baseUrl, HttpMethod.POST, requestEntity);
        JSONObject jsonData = new JSONObject(response.getBody());
        String accessToken = jsonData.get("access_token").toString();
        String refreshToken = jsonData.get("refresh_token").toString();
        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            log.warn("토큰 발급에 실패했습니다.");
            return false;
        } else {
            authToken = accessToken;
            return true;
        }
    }

    public String getAuthToken() {
        return authToken;
    }

}