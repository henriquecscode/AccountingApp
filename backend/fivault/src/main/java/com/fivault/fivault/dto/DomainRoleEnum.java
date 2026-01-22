package com.fivault.fivault.dto;


/**
 * Static roles
 */
public enum DomainRoleEnum {
    OWNER(1L),
    ADMIN(2L),
    MEMBER(3L),
    VIEWER(4L);

    private final Long domainRoleId;

    DomainRoleEnum(Long domainRoleId) {
        this.domainRoleId = domainRoleId;
    }

    public Long getDomainRoleId() {
        return domainRoleId;
    }
}
