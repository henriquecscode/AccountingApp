package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByPlatform_PlatformIdAndSlugStartingWith(Long platformId, String slugPrefix);

    List<Account> findByPlatform_PlatformId(Long platformId);

    Optional<Account> findByPlatform_PlatformIdAndSlug(Long platformId, String slug);

    Optional<Account> findByAccountId(Long accountId);
}
