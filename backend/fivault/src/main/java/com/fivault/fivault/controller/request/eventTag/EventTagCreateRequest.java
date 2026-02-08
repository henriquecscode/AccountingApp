package com.fivault.fivault.controller.request.eventTag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class EventTagCreateRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String description;

    private UUID parentEventTagId; //Can be null for parent tags

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getParentEventTagId() {
        return parentEventTagId;
    }
}
