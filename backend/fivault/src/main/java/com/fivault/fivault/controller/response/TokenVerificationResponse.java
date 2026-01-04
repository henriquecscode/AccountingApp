package com.fivault.fivault.controller.response;

public record TokenVerificationResponse(
        boolean valid,
        Long userId,
        String email,
        String message) {
}
