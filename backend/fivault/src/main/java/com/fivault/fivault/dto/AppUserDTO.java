package com.fivault.fivault.dto;

public class AppUserDTO {
    private Long appUserId;
    private String username;
    private String email;
    private String name;

    public AppUserDTO(Long appUserId, String username, String email, String name) {
        this.appUserId = appUserId;
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public Long getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(Long appUserId) {
        this.appUserId = appUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}