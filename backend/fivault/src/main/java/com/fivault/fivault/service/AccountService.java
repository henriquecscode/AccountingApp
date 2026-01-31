package com.fivault.fivault.service;

import com.fivault.fivault.database.model.Account;
import com.fivault.fivault.database.model.Platform;
import com.fivault.fivault.dto.AccountDTO;
import com.fivault.fivault.dto.DomainRoleEnum;
import com.fivault.fivault.mapper.AccountMapper;
import com.fivault.fivault.repository.AccountRepository;
import com.fivault.fivault.repository.PlatformRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.Account.AccountAccessResult;
import com.fivault.fivault.service.result.Account.AccountCreateResult;
import com.fivault.fivault.service.result.Account.AccountDetailResult;
import com.fivault.fivault.service.result.Account.AccountListResult;
import com.fivault.fivault.service.result.Platform.PlatformAccessResult;
import com.fivault.fivault.util.SlugUtil;
import com.fivault.fivault.util.StringUtil;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AccountService {
    private final DomainService domainService;
    private final PlatformService platformService;
    private final AccountRepository accountRepository;
    private final PlatformRepository platformRepository;
    private final EntityManager entityManager;
    private final AccountMapper accountMapper;

    public AccountService(DomainService domainService, PlatformService platformService, AccountRepository accountRepository, PlatformRepository platformRepository, EntityManager entityManager, AccountMapper accountMapper) {
        this.domainService = domainService;
        this.platformService = platformService;
        this.accountRepository = accountRepository;
        this.platformRepository = platformRepository;
        this.entityManager = entityManager;
        this.accountMapper = accountMapper;
    }

    @Transactional(readOnly = false)
    public Output<AccountCreateResult> createAccount(Long platformId, String accountName, String description) {
        // Step 1: Generate base slug

        if (accountName == null || accountName.trim().isEmpty()) {
            return Output.failure(ErrorCode.PLATFORM_CREATE_NO_NAME);
        }

        String baseSlug = SlugUtil.generateSlug(accountName);

        if (baseSlug == null || baseSlug.isBlank()) {
            return Output.failure(ErrorCode.PLATFORM_CREATE_INVALID_SLUG);
        }

        if (StringUtil.isValidUUID(baseSlug)) {
            return Output.failure(ErrorCode.PLATFORM_CREATE_INVALID_SLUG_UUID);
        }

        // Step 2: Fetch all slugs that start with this base for the domain
        List<Account> existingAccounts = accountRepository.findByPlatform_PlatformIdAndSlugStartingWith(platformId, baseSlug);

        // Step 3: Determine next available slug
        String slug = nextAvailableSlug(baseSlug, existingAccounts);

        // Step 4: Create the account
        Account account = new Account();
        account.setName(accountName);
        account.setSlug(slug);
        account.setPlatform(entityManager.getReference(Platform.class, platformId));
        account.setDescription(description);

        // Step 5: Persist
        accountRepository.save(account);

        return Output.success(new AccountCreateResult(slug));
    }

    @Transactional(readOnly = true)
    public Output<AccountDetailResult> getAccountDetail(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findByAccountId(accountId);

        if (accountOptional.isEmpty()) {
            return Output.failure(ErrorCode.ACCOUNT_FIND_BY_ACCOUNT_ID_ERROR);
        }
        Account account = accountOptional.get();
        // Get Information
        AccountDTO accountDTO = accountMapper.toDTO(account);

        return Output.success(
                new AccountDetailResult(accountDTO)
        );
    }


    @Transactional(readOnly = true)
    public Output<AccountListResult> getAccountList(Long platformId, Long appuserId) {
        // TODO use UserId to get only accounts with permission
        //TODO
        List<Account> accounts = accountRepository.findByPlatform_PlatformId(platformId);
        List<AccountDTO> accountDTOS = accounts.stream()
                .map(accountMapper::toDTO).toList();

        return Output.success(new AccountListResult(accountDTOS));
    }

    @Transactional(readOnly = true)
    public Output<AccountAccessResult> assertAccountReadAccess(String username, String domainOwner, String domainSlug, String platformSlug, String accountSlug) {
        return platformService.assertPlatformReadAccess(domainOwner, domainSlug, username, platformSlug)
                .flatMap(platformAccess ->
                        getAccount(platformAccess.platformid(), accountSlug)
                                .flatMap(account ->
                                        validateAccountAccess(platformAccess, account, ErrorCode.ACCOUNT_NO_READ_ACCESS, this::hasReadAccess)
                                )
                );
    }

    @Transactional(readOnly = true)
    public Output<AccountAccessResult> assertAccountAdminAccess(String username, String domainOwner, String domainSlug, String platformSlug, String accountSlug) {
        return platformService.assertPlatformAdminAccess(domainOwner, domainSlug, username, platformSlug)
                .flatMap(platformAccess ->
                        getAccount(platformAccess.platformid(), accountSlug)
                                .flatMap(account ->
                                        validateAccountAccess(platformAccess, account, ErrorCode.ACCOUNT_NO_ADMIN_ACCESS, this::hasAdminAccess)
                                )
                );
    }

    // Component 1: Get account
    private Output<Account> getAccount(Long platformId, String accountSlug) {
        Optional<Account> optionalAccount = accountRepository.findByPlatform_PlatformIdAndSlug(platformId, accountSlug);

        if (optionalAccount.isEmpty()) {
            return Output.failure(ErrorCode.ACCOUNT_FIND_BY_PLATFORM_SLUG_ERROR);
        }

        return Output.success(optionalAccount.get());
    }

    // Component 2: Account access validation
    private Output<AccountAccessResult> validateAccountAccess(
            PlatformAccessResult platformAccess,
            Account account,
            ErrorCode noAccessErrorCode,
            Function<Account, Boolean> accessChecker) {

        // TODO: Still have no feature for specific account access
        boolean hasAccess = accessChecker.apply(account);
        if (!hasAccess) {
            return Output.failure(noAccessErrorCode);
        }

        return Output.success(new AccountAccessResult(
                true,
                DomainRoleEnum.VIEWER,
                platformAccess.appUserId(),
                platformAccess.domainId(),
                platformAccess.platformid(),
                account.getAccountId()
        ));
    }

    // Component 3: Access checker functions
    private boolean hasReadAccess(Account account) {
        // TODO: Implement actual read access logic
        return true;
    }

    private boolean hasAdminAccess(Account account) {
        // TODO: Implement actual admin access logic
        return true;
    }

    /**
     * Given a base slug and a list of existing domains, determine the next available slug.
     * Example: baseSlug = "my-domain"
     * existing slugs = ["my-domain", "my-domain-2"]
     * returns "my-domain-3"
     */
    private String nextAvailableSlug(String baseSlug, List<Account> existinAccounts) {
        return SlugUtil.nextAvailableSlug(
                baseSlug,
                existinAccounts.stream().map(
                        Account::getSlug
                ).toList()
        );
    }

}
