package com.fivault.fivault.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String message,
        String deviceName
) {}
