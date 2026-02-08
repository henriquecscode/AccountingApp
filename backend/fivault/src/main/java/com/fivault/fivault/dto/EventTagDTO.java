package com.fivault.fivault.dto;

import java.util.UUID;

public record EventTagDTO(UUID eventTagId, String name, String description, UUID parentEventTagId) {
}
