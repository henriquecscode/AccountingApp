package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUsersDomains;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUsersDomainsRepository extends JpaRepository<AppUsersDomains, Long> {
    List<AppUsersDomains> findByAppUser(AppUser appUser);
}
