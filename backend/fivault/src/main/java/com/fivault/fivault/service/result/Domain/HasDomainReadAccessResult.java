package com.fivault.fivault.service.result.Domain;

import com.fivault.fivault.dto.DomainRoleEnum;

public record  HasDomainReadAccessResult(Boolean granted, DomainRoleEnum role, Long domainId) {
}
