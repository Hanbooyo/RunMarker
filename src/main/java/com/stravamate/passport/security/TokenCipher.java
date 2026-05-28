package com.stravamate.passport.security;

public interface TokenCipher {

    String encrypt(String plainText);

    String decrypt(String cipherText);
}
