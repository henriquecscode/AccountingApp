package com.fivault.fivault.service.result.domain;

import com.fivault.fivault.dto.DomainRoleEnum;

public record DomainAccessResult(Boolean granted, DomainRoleEnum role, Long domainId, Long appUserId) {
}
