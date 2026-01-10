package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUserSession;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface AppUserSessionRepository extends JpaRepository<AppUserSession, Long> {

    interface SessionInfo {
        AppUser getUser();

        LocalDateTime getExpiresAt();
    }

    Optional<SessionInfo> findByTokenHash(String tokenHash);
}
