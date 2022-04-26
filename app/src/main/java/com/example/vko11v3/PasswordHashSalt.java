package com.example.vko11v3;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashSalt {
    public static String getSecurePassword(String password, byte[] salt) {

        String generatedPassword = null;
        try {
            // Apply SHA-512 to password
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(salt);
            byte[] bytes = digest.digest(password.getBytes());
            StringBuilder string = new StringBuilder();
            for (byte aByte : bytes) {
                string.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = string.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    // Returns the salt for HASHING
    public static byte[] getSalt() {
        return "saltForHashing".getBytes(StandardCharsets.UTF_8);
    }
}
