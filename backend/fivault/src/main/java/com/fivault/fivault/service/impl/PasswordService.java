package com.fivault.fivault.service.impl;

import com.fivault.fivault.service.HashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder;

    @Value("${security.password.pepper}")
    private String pepper;  // Global secret, stored in config

    public PasswordService() {
        this.encoder = new BCryptPasswordEncoder(12);  // Work factor of 12
    }

    /**
     * Hash password with HMAC-SHA256 (using pepper as key) then bcrypt
     * Pattern: bcrypt(hmac_sha256(password, pepper))
     *
     * Bcrypt handles salting automatically (generates random salt and stores it in the hash)
     * Pepper: global secret HMAC key, stored in configuration
     */

    public String hashPassword(String plainPassword) {
        if (plainPassword == null || pepper == null) {
            throw new IllegalArgumentException("Password and pepper cannot be null");
        }
        String hmacHash = hmacSha256Base64(plainPassword, pepper);
        return encoder.encode(hmacHash);
    }

    /**
     * Verify password against stored hash
     * Bcrypt extracts the salt from the stored hash automatically
     */
    public boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || pepper == null || storedHash == null) {
            throw new IllegalArgumentException("Password, pepper, and stored hash cannot be null");
        }

        String hmacHash = hmacSha256Base64(plainPassword, pepper);
        return encoder.matches(hmacHash, storedHash);
    }

    /**
     * Compute HMAC-SHA256 and return as Base64
     * @param message the message to hash (password)
     * @param key the secret key (pepper)
     * @return Base64-encoded HMAC
     */
    private String hmacSha256Base64(String message, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("HmacSHA256 algorithm not available", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid pepper key for HMAC", e);
        }
    }

    public boolean testPasswordStrength(String password) {
        return true;
    }
}