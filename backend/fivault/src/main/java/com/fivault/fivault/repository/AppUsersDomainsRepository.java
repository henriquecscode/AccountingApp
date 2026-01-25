package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUsersDomains;
import com.fivault.fivault.database.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUsersDomainsRepository extends JpaRepository<AppUsersDomains, Long> {
    List<AppUsersDomains> findByAppUser(AppUser appUser);

    List<AppUsersDomains> findByDomain(Domain domain);

    Optional<AppUsersDomains> findByDomainAndAppUser(Domain domain, AppUser appUser);
}
