package com.fivault.fivault.controller.response.auth;

public record TokenVerificationResponse(
        boolean valid,
        Long userId,
        String email,
        String message) {
}
