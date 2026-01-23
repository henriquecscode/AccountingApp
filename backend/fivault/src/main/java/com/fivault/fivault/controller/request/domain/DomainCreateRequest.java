package com.fivault.fivault.controller.request.domain;

import com.fivault.fivault.service.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DomainCreateRequest {
    @NotBlank()
    private String domainName;


    @Size(max = 500)
    private String description;
    public String getDomainName() {
        return domainName;
    }

    public String getDescription() {
        return description;
    }
}