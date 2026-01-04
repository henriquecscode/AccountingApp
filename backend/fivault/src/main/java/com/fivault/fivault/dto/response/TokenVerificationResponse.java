package com.fivault.fivault.dto.response;

public record TokenVerificationResponse(
        boolean valid,
        Long userId,
        String email,
        String message) {
}
