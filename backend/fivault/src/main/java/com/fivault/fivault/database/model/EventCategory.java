package com.fivault.fivault.database.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "domain_event_categories",
        indexes = {
                @Index(name = "idx_event_categories_parent_event_category", columnList = "parent_event_category_id"),
                @Index(name = "idx_event_categories_domain", columnList = "domain_id")
        }
)
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_category_id")
    private UUID eventCategoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_event_category_id", nullable = true)
    private EventCategory parentEventCategory;

    @Column(name = "is_removed")
    @ColumnDefault("false")
    private Boolean isRemoved;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getEventCategoryId() {
        return eventCategoryId;
    }

    public void setEventCategoryId(UUID eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
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

    public EventCategory getParentEventCategory() {
        return parentEventCategory;
    }

    public void setParentEventCategory(EventCategory parentEventCategory) {
        this.parentEventCategory = parentEventCategory;
    }

    public Boolean getRemoved() {
        return isRemoved;
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
