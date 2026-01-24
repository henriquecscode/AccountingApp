package com.fivault.fivault.controller.response.domain;

import com.fivault.fivault.dto.VisibleDomainDTO;
import com.fivault.fivault.service.output.Domain.ListDomainsResult;

import java.util.List;

public record DomainListResponse(List<VisibleDomainDTO> ownedDomains, List<VisibleDomainDTO> nonOwnedDomains) {
    public static DomainListResponse from(ListDomainsResult result) {
        return new DomainListResponse(
                result.ownedDomains(),
                result.nonOwnedDomains()
        );
    }
}
