package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {


    List<Platform> findByDomainAndSlugStartingWith(Domain domain, String slugPrefix);

    Optional<Platform> findByDomain_DomainIdAndSlug(Long domainId, String platformSlug);

    List<Platform> findByDomain(Domain domain);

    Optional<Platform> findByPlatformId(Long platformId);

    boolean existsByPlatformId(Long platformId);
}
