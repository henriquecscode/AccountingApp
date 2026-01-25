package com.fivault.fivault.controller.request.platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlatformCreateRequest {
    @NotBlank()
    private String platformName;

    @Size(max = 500)
    private String description;

    public String getPlatformName() {
        return platformName;
    }

    public String getDescription() {
        return description;
    }
}
