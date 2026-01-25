package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {
    boolean existsByOwnerAndSlug(AppUser owner, String slug);

    Optional<Domain> findByOwnerAndSlug(AppUser owner, String slug);

    List<Domain> findByOwnerAndSlugStartingWith(AppUser owner, String slugPrefix);

    Optional<Domain> findByDomainId(Long domainId);
}
