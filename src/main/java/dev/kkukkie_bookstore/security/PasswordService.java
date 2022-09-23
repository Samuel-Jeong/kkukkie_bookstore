package dev.kkukkie_bookstore.security;

import dev.kkukkie_bookstore.model.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.model.IComment;
import org.thymeleaf.processor.comment.ICommentProcessor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.LinkOption;
import java.security.*;
import java.util.Base64;
import java.util.Random;

/**
 * AES256
 */

@Slf4j
@Service
public class PasswordService {

    private final String ALGORITHM = "AES";
    private final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final byte[] IV = new byte[] {35, -76, -13, 26, 57, -52, -50, 96, -100, 18, 34, 79, 64, -108, -77, 35};
    private final String KEY = "20200829";

    public String encryptPassword(String content, Member member) {
        return getEncrypt(content, member);
    }

    public String decryptPassword(String content, Member member) {
        return getDecrypt(content, member);
    }

    private Key getAesKey() {
        Key key = null;
        try {
            byte[] keyBytes = new byte[16];
            byte[] b = KEY.getBytes(StandardCharsets.UTF_8);
            int length = b.length;
            if (length > keyBytes.length) {
                length = keyBytes.length;
            }
            System.arraycopy(b, 0, keyBytes, 0, length);
            key = new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            log.warn("Fail to get the key", e);
        }
        return key;
    }

    /*public IvParameterSpec getIv() {
        Random rand = new SecureRandom();
        byte[] bytes = new byte[16];
        rand.nextBytes(bytes);
        return new IvParameterSpec(bytes);
    }*/

    private String getEncrypt(String content, Member member) {
        String result = "";
        try {
            Key key = getAesKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);

            //member.setIvParameterSpec(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

            byte[] encryptedContent = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            result = new String(Base64.getEncoder().encode(encryptedContent));
        } catch (Exception e) {
            log.warn("Fail to encrypt the content", e);
        }
        return result;
    }

    private String getDecrypt(String content, Member member) {
        String result = "";
        try {
            Key key = getAesKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

            byte[] decodedContent = Base64.getDecoder().decode(content.getBytes());
            result = new String(cipher.doFinal(decodedContent), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Fail to decrypt the content.", e);
        }
        return result;
    }

}
