package com.fivault.fivault.service.result.account;

import com.fivault.fivault.dto.DomainRoleEnum;

public record AccountAccessResult(Boolean granted, DomainRoleEnum role, Long selfAppUserId, Long domainId, Long platformId, Long accountId) {
}
