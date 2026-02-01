package com.fivault.fivault.controller.request.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public class EventCreateRequest {

    @NotBlank()
    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String description;

    private OffsetDateTime startTimestamp;

    @NotNull
    private OffsetDateTime endTimestamp;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public OffsetDateTime getEndTimestamp() {
        return endTimestamp;
    }
}
