package com.fivault.fivault.controller.request.eventTag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class EventTagUpdateRequest {

    @NotBlank
    private UUID eventTagId;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String description;

    private UUID parentEventTagId; //Can be null for parent tags

    public UUID getEventTagId() {
        return eventTagId;
    }

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
