package com.fivault.fivault.controller.response;

public record SignUpResponse(
        String accessToken,
        String message,
        String deviceName
) {}
