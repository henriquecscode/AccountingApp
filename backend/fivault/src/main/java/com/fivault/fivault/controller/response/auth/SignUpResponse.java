package com.fivault.fivault.controller.response.auth;

public record SignUpResponse(
        String accessToken,
        String message,
        String deviceName
) {}
