package com.stravamate.passport.security;

import com.stravamate.passport.config.AppProperties;
import com.stravamate.passport.exception.AuthException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@ConditionalOnProperty(name = "app.security.token-cipher", havingValue = "aes-gcm")
public class AesGcmTokenCipher implements TokenCipher {

    private static final String PREFIX = "v1";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec secretKeySpec;

    public AesGcmTokenCipher(AppProperties appProperties) {
        String encodedKey = appProperties.security() == null ? null : appProperties.security().tokenEncryptionKey();
        if (!StringUtils.hasText(encodedKey)) {
            throw new AuthException("TOKEN_ENCRYPTION_KEY 환경변수가 필요합니다.");
        }

        byte[] key = Base64.getDecoder().decode(encodedKey);
        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new AuthException("TOKEN_ENCRYPTION_KEY는 Base64로 인코딩된 16, 24, 32바이트 AES 키여야 합니다.");
        }

        this.secretKeySpec = new SecretKeySpec(key, ALGORITHM);
    }

    @Override
    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return PREFIX + ":"
                    + Base64.getEncoder().encodeToString(iv) + ":"
                    + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception exception) {
            throw new AuthException("토큰 암호화에 실패했습니다.", exception);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        if (cipherText == null) {
            return null;
        }
        if (!cipherText.startsWith(PREFIX + ":")) {
            throw new AuthException("암호화되지 않은 기존 토큰입니다. 운영 암호화 적용 후 다시 Strava 로그인이 필요합니다.");
        }

        try {
            String[] parts = cipherText.split(":", 3);
            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] encrypted = Base64.getDecoder().decode(parts[2]);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new AuthException("토큰 복호화에 실패했습니다.", exception);
        }
    }
}
