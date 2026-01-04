package com.fivault.fivault.service;

import com.fivault.fivault.util.RandomUtil;

public interface HashService {
    /**
     * Generate a cryptographically secure random salt
     */
    default String generateSalt() {
        return RandomUtil.randomBase64(32);
    }

    String hashPassword(String plainPassword, String salt);

    boolean verifyPassword(String plainPassword, String salt, String storedHash);
}
