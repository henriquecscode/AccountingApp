package com.fivault.fivault.controller.request.event;

import jakarta.validation.constraints.Size;

public class EventCreateRequest {

    @Size(max= 255)
    private String title;

    @Size(max = 500)
    private String description;
}
