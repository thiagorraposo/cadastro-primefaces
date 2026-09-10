package com.devmedia.service;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SenhaHash {
    private SenhaHash() { }
    public static String gerar(String senha) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        char[] caracteres = senha.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(caracteres, salt, 600_000, 256);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return "pbkdf2-sha256$600000$" + Base64.getEncoder().encodeToString(salt)
                    + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível proteger a senha.", e);
        } finally { spec.clearPassword(); Arrays.fill(caracteres, '\0'); }
    }
}
