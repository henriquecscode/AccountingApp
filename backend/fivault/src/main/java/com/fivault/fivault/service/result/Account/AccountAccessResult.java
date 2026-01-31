package com.fivault.fivault.service.result.Account;

import com.fivault.fivault.dto.DomainRoleEnum;

public record AccountAccessResult(Boolean granted, DomainRoleEnum role, Long selfAppUserId, Long domainId, Long platformId, Long accountId) {
}
