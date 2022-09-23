package dev.kkukkie_bookstore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao")
@Getter
@Setter
public class KakaoConfig {

    private String baseUrl;
    private String appKey;
    private String redirectUri;
    private String clientSecret;
    private long keyTimeout;

}
