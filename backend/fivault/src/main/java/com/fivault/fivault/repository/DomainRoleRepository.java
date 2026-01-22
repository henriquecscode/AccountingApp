package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.DomainRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomainRoleRepository extends JpaRepository<DomainRole, Long> {

    Optional<DomainRole> findByDomainRoleId(Long domainRoleId);
}
