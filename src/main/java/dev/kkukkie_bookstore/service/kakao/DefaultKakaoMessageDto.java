package dev.kkukkie_bookstore.service.kakao;

import lombok.Data;

@Data
public class DefaultKakaoMessageDto {

    private String objType;
    private String text;
    private String webUrl;
    private String mobileUrl;
    private String btnTitle;

}