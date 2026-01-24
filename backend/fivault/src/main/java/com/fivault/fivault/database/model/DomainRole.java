package com.fivault.fivault.database.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "domain_roles")
public class DomainRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long domainRoleId;

    @Column(length = 10, nullable = false, unique = true, updatable = false)
    private String code; // 'ADMIN', 'MEMBER', etc.

    @Column(length = 30, nullable = false)
    private String name; // 'Administrator', etc.

    @Column
    private String description; // Longer explanation

    public Long getDomainRoleId() {
        return domainRoleId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
