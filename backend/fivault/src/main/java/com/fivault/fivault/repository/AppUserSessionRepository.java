package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserSessionRepository extends JpaRepository<AppUserSession, Long> {
}
