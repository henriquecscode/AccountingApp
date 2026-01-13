package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.AppUsersDomains;
import com.fivault.fivault.database.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUsersDomainsRepository extends JpaRepository<AppUsersDomains, Long> {

}
