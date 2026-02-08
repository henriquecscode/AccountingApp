package com.fivault.fivault.database.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "platformaccounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_platformaccounts_platform_slug", columnNames = {"platform_id", "slug"})
        })
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="slug", nullable = false)
    private String slug;

    @Column(name="description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
