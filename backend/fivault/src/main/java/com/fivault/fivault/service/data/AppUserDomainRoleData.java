package com.fivault.fivault.service.data;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.DomainRole;

public record AppUserDomainRoleData(AppUser user, Domain domain, DomainRole role) {
}
