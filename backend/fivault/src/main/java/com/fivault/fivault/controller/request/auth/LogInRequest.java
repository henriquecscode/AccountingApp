package com.fivault.fivault.controller.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LogInRequest {
    @NotBlank()
    @Size(max = 255)
    String username;

    @NotBlank
    @Size(max = 255)
    String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
