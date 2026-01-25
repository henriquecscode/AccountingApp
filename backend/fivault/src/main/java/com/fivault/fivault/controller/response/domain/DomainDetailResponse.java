package com.fivault.fivault.controller.response.domain;

import com.fivault.fivault.dto.AppUserDomainRole;
import com.fivault.fivault.dto.DomainDTO;
import com.fivault.fivault.dto.PlatformDTO;

import java.util.List;


public record DomainDetailResponse(DomainDTO domainDTO, List<AppUserDomainRole> domainAppUsers, List<PlatformDTO> platformDTOS) {
}
