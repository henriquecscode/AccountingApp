package com.fivault.fivault.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class DeviceFingerprint {

    public DeviceFingerprint() {
    }

    /**
     * Generate a unique device ID based on user agent and other factors
     * Note: This is a basic implementation. For production, consider using
     * a library like FingerprintJS or similar
     */
    public String generateDeviceId(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String acceptLanguage = request.getHeader("Accept-Language");
        String acceptEncoding = request.getHeader("Accept-Encoding");

        // Combine headers to create a fingerprint
        String fingerprint = String.format("%s|%s|%s",
                userAgent != null ? userAgent : "",
                acceptLanguage != null ? acceptLanguage : "",
                acceptEncoding != null ? acceptEncoding : ""
        );

        return hashFingerprint(fingerprint);
    }

    /**
     * Extract a human-readable device name from user agent
     */
    public String extractDeviceName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown Device";
        }

        // Mobile devices
        if (userAgent.contains("iPhone")) return "iPhone";
        if (userAgent.contains("iPad")) return "iPad";
        if (userAgent.contains("Android")) {
            if (userAgent.contains("Mobile")) return "Android Phone";
            return "Android Tablet";
        }

        // Desktop browsers
        if (userAgent.contains("Windows")) return "Windows PC";
        if (userAgent.contains("Macintosh")) return "Mac";
        if (userAgent.contains("Linux")) return "Linux PC";

        // Browsers
        if (userAgent.contains("Chrome")) return "Chrome Browser";
        if (userAgent.contains("Firefox")) return "Firefox Browser";
        if (userAgent.contains("Safari")) return "Safari Browser";
        if (userAgent.contains("Edge")) return "Edge Browser";

        return "Unknown Device";
    }

    private String hashFingerprint(String fingerprint) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fingerprint.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
