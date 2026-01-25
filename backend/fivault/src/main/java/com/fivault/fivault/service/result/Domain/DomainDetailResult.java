package com.fivault.fivault.service.result.Domain;

import com.fivault.fivault.dto.AppUserDomainRole;
import com.fivault.fivault.dto.DomainDTO;

import java.util.List;


public record DomainDetailResult(DomainDTO domain, List<AppUserDomainRole> domainAppUsers) {


}
