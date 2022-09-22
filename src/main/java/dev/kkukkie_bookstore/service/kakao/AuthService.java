package dev.kkukkie_bookstore.service.kakao;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
public class AuthService extends HttpCallService{

    private static final String AUTH_URL = "https://kauth.kakao.com/oauth/token";

    public static String authToken;

    public boolean getKakaoAuthToken(String code) throws JSONException {
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", APP_TYPE_URL_ENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", "334597f4502c5ce340c507ce0126502f"); // REST API KEY
        parameters.add("redirect_uri", "http://localhost:8080/auth");
        parameters.add("client_secret", "Hl8IpCZLWueA2eDPxrTtLrYi9K2nA4pr"); // Client secret

        HttpEntity<?> requestEntity = httpClientEntity(header, parameters);

        ResponseEntity<String> response = httpRequest(AUTH_URL, HttpMethod.POST, requestEntity);
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

}