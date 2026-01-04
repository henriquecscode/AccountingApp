package com.fivault.fivault.dto.response;

public record AuthResponse(
        String accessToken,
        String message,
        String deviceName
) {}
