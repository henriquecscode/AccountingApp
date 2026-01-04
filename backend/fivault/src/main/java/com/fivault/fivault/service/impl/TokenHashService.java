package com.fivault.fivault.service.impl;

import com.fivault.fivault.util.RandomUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class TokenHashService {


    public String generateSalt() {
        return RandomUtil.randomBase64(32);
    }

    /**
     * Hash a token using SHA-256 with salt
     * @param plainToken The plain text token
     * @param salt The salt (unique per token)
     * @return Base64-encoded hash of (salt + token)
     */

    public String hashData(String plainToken, String salt) {
        try {
            // Combine salt and token
            String combined = salt + plainToken;

            // Get SHA-256 instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hash the combined string
            byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            // Encode to Base64 for storage
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verify a plain token against a stored hash
     * @param plainToken The token from the user's request
     * @param salt The salt used when creating the hash
     * @param storedHash The hash stored in the database
     * @return true if the token is valid
     */
    public boolean verifyData(String plainToken, String salt, String storedHash) {
        // Hash the provided token with the same salt
        String computedHash = hashData(plainToken, salt);

        // Compare with stored hash (constant-time comparison to prevent timing attacks)
        return MessageDigest.isEqual(
                computedHash.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}