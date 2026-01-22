package com.fivault.fivault.controller.response.auth;

public record LogInResponse(
        String accessToken,
        String message,
        String deviceName
) {}

