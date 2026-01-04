package com.fivault.fivault.controller.response;

public record SignInResponse(
        String accessToken,
        String message,
        String deviceName
) {}

