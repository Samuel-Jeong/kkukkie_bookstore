package dev.kkukkie_bookstore.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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

    private final IvParameterSpec IV;
    private final String KEY = "20200829";

    public PasswordService() {
        Random rand = new SecureRandom();
        byte[] bytes = new byte[16];
        rand.nextBytes(bytes);
        IV = new IvParameterSpec(bytes);
    }

    public String encryptPassword(String content) {
        return getEncrypt(content);
    }

    public String decryptPassword(String content) {
        return getDecrypt(content);
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

    private String getEncrypt(String content) {
        String result = "";
        try {
            Key key = getAesKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, IV);

            byte[] encryptedContent = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            result = new String(Base64.getEncoder().encode(encryptedContent));
        } catch (Exception e) {
            log.warn("Fail to encrypt the content", e);
        }
        return result;
    }

    private String getDecrypt(String content) {
        String result = "";
        try {
            Key key = getAesKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, IV);

            byte[] decodedContent = Base64.getDecoder().decode(content.getBytes());
            result = new String(cipher.doFinal(decodedContent), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Fail to decrypt the content.", e);
        }
        return result;
    }

}
