package com.stravamate.passport.security;

import org.springframework.stereotype.Component;

@Component
public class NoOpTokenCipher implements TokenCipher {

    @Override
    public String encrypt(String plainText) {
        return plainText;
    }

    @Override
    public String decrypt(String cipherText) {
        return cipherText;
    }
}
