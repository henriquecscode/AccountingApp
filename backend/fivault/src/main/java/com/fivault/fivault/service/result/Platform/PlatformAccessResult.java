package com.fivault.fivault.service.result.Platform;

public record PlatformAccessResult(Boolean granted, Long platformid, Long domainId, Long appUserId) {
}
