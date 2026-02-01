package com.fivault.fivault.service.result.domain;

import com.fivault.fivault.dto.VisibleDomainDTO;

import java.util.List;

public record ListDomainsResult(List<VisibleDomainDTO> ownedDomains, List<VisibleDomainDTO> nonOwnedDomains) {
}
