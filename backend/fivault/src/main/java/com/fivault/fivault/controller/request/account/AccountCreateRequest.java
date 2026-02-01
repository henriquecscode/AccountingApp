package com.fivault.fivault.controller.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccountCreateRequest {
    @NotBlank()
    @Size(max = 255)
    private String accountName;

    @Size(max = 255)
    private String description;

    public String getAccountName() {
        return accountName;
    }

    public String getDescription() {
        return description;
    }
}
