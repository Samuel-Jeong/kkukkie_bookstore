package dev.kkukkie_bookstore.service.kakao;

import dev.kkukkie_bookstore.service.admin.AdminAuthService;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomKakaoMessageService {

    private final AdminAuthService authService;

    private final MessageService messageService;

    public CustomKakaoMessageService(AdminAuthService authService, MessageService messageService) {
        this.authService = authService;
        this.messageService = messageService;
    }

    public String sendMyMessage() throws JSONException {
        DefaultKakaoMessageDto myMsg = new DefaultKakaoMessageDto();
        myMsg.setBtnTitle("");
        myMsg.setMobileUrl("");
        myMsg.setObjType("text");
        myMsg.setWebUrl("");

        // AdminAuthService 에 UUID 등록
        String authCode = UUID.randomUUID().toString().substring(0, 10);
        authService.addAuthCode(authCode);
        myMsg.setText(authCode);

        String accessToken = KakaoAuthService.authToken;
        return messageService.sendMessage(accessToken, myMsg)? authCode : "";
    }
}
