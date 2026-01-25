package com.fivault.fivault.controller.response.domain;

import com.fivault.fivault.dto.AppUserDomainRole;
import com.fivault.fivault.dto.DomainDTO;

import java.util.List;


public record DomainDetailResponse(DomainDTO domainDTO, List<AppUserDomainRole> domainAppUsers) {
}
