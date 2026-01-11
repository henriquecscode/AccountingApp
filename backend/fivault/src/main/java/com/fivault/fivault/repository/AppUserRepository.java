package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUsername(String username);
}
