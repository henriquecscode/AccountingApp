package com.fivault.fivault.dto;

import java.util.UUID;

public record EventCategoryDTO(UUID eventCategoryId, String name, String description, UUID parentEventCategoryId) {
}
