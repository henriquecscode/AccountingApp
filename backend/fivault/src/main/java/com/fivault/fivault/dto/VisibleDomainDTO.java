package com.fivault.fivault.dto;

public record VisibleDomainDTO(
        String ownerName,
        String domainName,
        String domainSlug,
        String domainDescription,
        String selfDomainRoleCode
) {
}
