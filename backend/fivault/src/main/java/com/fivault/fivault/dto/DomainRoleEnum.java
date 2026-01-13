package com.fivault.fivault.dto;


public enum DomainRuleEnum {
    OWNER(1L),
    ADMIN(2L),
    MEMBER(3L),
    VIEWER(4L);

    private final Long domainRoleId;

    DomainRuleEnum(Long domainRoleId) {
        this.domainRoleId = domainRoleId;
    }

    public Long getDomainRoleId() {
        return domainRoleId;
    }
}

public class DomainRoleDTO {
}
