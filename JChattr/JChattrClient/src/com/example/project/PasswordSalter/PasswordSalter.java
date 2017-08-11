package com.example.project.PasswordSalter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordSalter {

        public String getHash(String password, String salt) {
            try {
                String toHash = salt + password;
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] encodedHash = messageDigest.digest(toHash.getBytes(StandardCharsets.UTF_8));
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < encodedHash.length; i++) {
                    String hex = Integer.toHexString(0xff & encodedHash[i]);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException nsae) {
                nsae.printStackTrace();
                return null;
            }
        }

        public String getRandomSalt() {
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            String saltResult = Base64.getEncoder().encodeToString(salt);
            return saltResult;
        }
}
