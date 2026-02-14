package com.fivault.fivault.controller.request.eventCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class EventCategoryCreateUpdateRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String description;

    private UUID parentEventCategoryId; //Can be null for parent categories

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getParentEventCategoryId() {
        return parentEventCategoryId;
    }
}
