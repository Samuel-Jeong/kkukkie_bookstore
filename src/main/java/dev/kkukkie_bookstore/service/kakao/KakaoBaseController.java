package dev.kkukkie_bookstore.service.kakao;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class KakaoBaseController {

    KakaoAuthService kakaoAuthService;

    CustomKakaoMessageService customKakaoMessageService;

    public KakaoBaseController(KakaoAuthService kakaoAuthService, CustomKakaoMessageService customKakaoMessageService) {
        this.kakaoAuthService = kakaoAuthService;
        this.customKakaoMessageService = customKakaoMessageService;
    }

    @GetMapping("/auth")
    public String serviceStart(String code, Model model) throws JSONException {
        if (kakaoAuthService.getKakaoAuthToken(code)) {
            String authCode = customKakaoMessageService.sendMyMessage();
            model.addAttribute("authCode", authCode);
        }

        return "members/adminAuthCodeModal";
    }

}
