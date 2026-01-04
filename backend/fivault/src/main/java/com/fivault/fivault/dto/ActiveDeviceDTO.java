package com.fivault.fivault.dto;

public record ActiveDeviceDTO(
        String deviceId,
        String deviceName,
        String ipAddress,
        String location,
        java.time.LocalDateTime lastUsedAt,
        java.time.LocalDateTime createdAt
) {}