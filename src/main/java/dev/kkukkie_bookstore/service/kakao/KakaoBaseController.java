package dev.kkukkie_bookstore.service.kakao;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoBaseController {

    AuthService authService;

    CustomMessageService customMessageService;

    public KakaoBaseController(AuthService authService, CustomMessageService customMessageService) {
        this.authService = authService;
        this.customMessageService = customMessageService;
    }

    @GetMapping("/auth")
    public String serviceStart(String code) throws JSONException {
        if(authService.getKakaoAuthToken(code)) {
            customMessageService.sendMyMessage();
            return "메시지 전송 성공";
        }else {
            return "토큰발급 실패";
        }
    }

}
