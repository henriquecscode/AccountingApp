package com.fivault.fivault.service.result.platform;

public record PlatformAccessResult(Boolean granted, Long platformid, Long domainId, Long appUserId) {
}
