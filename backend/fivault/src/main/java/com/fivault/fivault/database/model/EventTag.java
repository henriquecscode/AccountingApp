package com.fivault.fivault.database.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "domain_event_tags",
        indexes = {
                @Index(name = "idx_event_tags_parent_event_tag", columnList = "parent_event_tag_id"),
                @Index(name = "idx_event_tags_domain", columnList = "domain_id"),
                @Index(name = "idx_event_tags_start_timestamp", columnList = "start_timestamp")
        }
)
public class EventTag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventTagId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="domain_id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_event_tag_id", nullable = true)
    private EventTag parentEventTag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getEventTagId() {
        return eventTagId;
    }

    public void setEventTagId(UUID eventTagId) {
        this.eventTagId = eventTagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public EventTag getParentEventTag() {
        return parentEventTag;
    }

    public void setParentEventTag(EventTag parentEventTag) {
        this.parentEventTag = parentEventTag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
