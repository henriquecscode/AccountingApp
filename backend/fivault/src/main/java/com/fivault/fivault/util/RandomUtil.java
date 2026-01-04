package com.fivault.fivault.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomUtil {
    // Singleton SecureRandom instance
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomUtil() {} // prevent instantiation

    /**
     * Generate secure random bytes.
     * @param length number of bytes
     * @return byte array
     */
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    /**
     * Generate a base64 URL-safe string.
     * @param length number of bytes before encoding
     * @return base64url string
     */
    public static String randomBase64Url(int length) {
        byte[] bytes = randomBytes(length);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Generate a base64 string.
     * @param length number of bytes before encoding
     * @return base64 string
     */
    public static String randomBase64(int length){
        byte[] bytes = randomBytes(length);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
