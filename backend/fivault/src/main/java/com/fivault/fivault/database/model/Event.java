package com.fivault.fivault.database.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "domain_events",
        indexes = {
                @Index(name = "idx_events_parent_event", columnList = "parent_event_id"),
                @Index(name = "idx_events_domain", columnList = "domain_id"),
                @Index(name = "idx_events_start_timestamp", columnList = "start_timestamp")
        }
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name="title", length = 500, nullable = false)
    private String title;

    @Column(name="description",columnDefinition = "TEXT") // Equivalent to @Lob
    private String description;

    @Column(name="start_timestamp")
    private OffsetDateTime startTimestamp;

    @Column(name="end_timestamp")
    private OffsetDateTime endTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="domain_id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_event_id", nullable = true)
    private Event parentEvent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(OffsetDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public OffsetDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(OffsetDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Event getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(Event parentEvent) {
        this.parentEvent = parentEvent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
