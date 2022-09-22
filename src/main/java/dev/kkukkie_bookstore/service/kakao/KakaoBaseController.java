package dev.kkukkie_bookstore.service.kakao;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoBaseController {

    KakaoAuthService kakaoAuthService;

    CustomKakaoMessageService customKakaoMessageService;

    public KakaoBaseController(KakaoAuthService kakaoAuthService, CustomKakaoMessageService customKakaoMessageService) {
        this.kakaoAuthService = kakaoAuthService;
        this.customKakaoMessageService = customKakaoMessageService;
    }

    @GetMapping("/auth")
    public String serviceStart(String code)
            throws JSONException {
        if (kakaoAuthService.getKakaoAuthToken(code)) {
            customKakaoMessageService.sendMyMessage();
        }
        return "redirect:/";
    }

}
