package com.fivault.fivault.service.params.event;

import java.time.OffsetDateTime;

public record EventCreateParams(Long domainId, String title, String description, OffsetDateTime startTimestamp, OffsetDateTime endTimestamp) {
}
