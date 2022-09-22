package dev.kkukkie_bookstore.service.kakao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomMessageService {

    MessageService messageService;

    public CustomMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public boolean sendMyMessage() throws JSONException {
        DefaultMessageDto myMsg = new DefaultMessageDto();
        myMsg.setBtnTitle("");
        myMsg.setMobileUrl("");
        myMsg.setObjType("text");
        myMsg.setWebUrl("");
        myMsg.setText(UUID.randomUUID().toString());

        String accessToken = AuthService.authToken;
        return messageService.sendMessage(accessToken, myMsg);
    }
}
