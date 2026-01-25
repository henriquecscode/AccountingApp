package com.fivault.fivault.service;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUsersDomains;
import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.DomainRole;
import com.fivault.fivault.dto.AppUserDomainRole;
import com.fivault.fivault.dto.DomainDTO;
import com.fivault.fivault.dto.DomainRoleEnum;
import com.fivault.fivault.dto.VisibleDomainDTO;
import com.fivault.fivault.mapper.DomainMapper;
import com.fivault.fivault.repository.AppUserRepository;
import com.fivault.fivault.repository.AppUsersDomainsRepository;
import com.fivault.fivault.repository.DomainRepository;
import com.fivault.fivault.repository.DomainRoleRepository;
import com.fivault.fivault.service.data.AppUserDomainRoleData;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.Domain.CreateDomainResult;
import com.fivault.fivault.service.result.Domain.GetDomainsResult;
import com.fivault.fivault.service.result.Domain.HasDomainReadAccessResult;
import com.fivault.fivault.service.result.Domain.ListDomainsResult;
import com.fivault.fivault.service.result.Domain.DomainDetailResult;
import com.fivault.fivault.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DomainService {

    private static List<DomainRoleEnum> viewRoles = Arrays.asList(
            DomainRoleEnum.OWNER,
            DomainRoleEnum.ADMIN,
            DomainRoleEnum.MEMBER,
            DomainRoleEnum.VIEWER);

    private final AppUserRepository appUserRepository;
    private final DomainRepository domainRepository;
    private final AppUsersDomainsRepository appUsersDomainsRepository;
    private final DomainRoleRepository domainRoleRepository;
    private final DomainMapper domainMapper;

    public DomainService(AppUserRepository appUserRepository, DomainRepository domainRepository, AppUsersDomainsRepository appUsersDomainsRepository, DomainRoleRepository domainRoleRepository, DomainMapper domainMapper) {
        this.appUserRepository = appUserRepository;
        this.domainRepository = domainRepository;
        this.appUsersDomainsRepository = appUsersDomainsRepository;
        this.domainRoleRepository = domainRoleRepository;
        this.domainMapper = domainMapper;
    }

    public Output<GetDomainsResult> getDomains(Long appUserId) {


        return Output.success(new GetDomainsResult());
    }

    @Transactional(readOnly = false)
    public Output<CreateDomainResult> createDomain(String ownerUsername, String name, String description) {
        // Step 1: Generate base slug
        Optional<AppUser> ownerOptional = appUserRepository.findByUsername(ownerUsername);
        if (ownerOptional.isEmpty()) {
            return Output.failure(ErrorCode.APPUSER_FIND_BY_USERNAME_ERROR);
        }
        AppUser owner = ownerOptional.get();

        if (name == null || name.trim().isEmpty()) {
            return Output.failure(ErrorCode.DOMAIN_CREATE_NO_NAME);
        }

        String baseSlug = SlugUtil.generateSlug(name);

        if (baseSlug == null || baseSlug.isBlank()) {
            return Output.failure(ErrorCode.DOMAIN_CREATE_INVALID_SLUG);
        }
        // Step 2: Fetch all slugs that start with this base for the owner
        List<Domain> existingDomains = domainRepository.findByOwnerAndSlugStartingWith(owner, baseSlug);

        // Step 3: Determine next available slug
        String slug = nextAvailableSlug(baseSlug, existingDomains);

        // Step 4: Create the domain
        Domain domain = new Domain();
        domain.setName(name);
        domain.setSlug(slug);
        domain.setOwner(owner);
        domain.setDescription(description);

        Optional<DomainRole> domainRoleOptional = domainRoleRepository.findByDomainRoleId(DomainRoleEnum.OWNER.getDomainRoleId());

        if (!domainRoleOptional.isPresent()) {
            return Output.failure(ErrorCode.DOMAIN_NO_OWNER_ROLE);
        }

        AppUsersDomains appUsersDomains = new AppUsersDomains();
        appUsersDomains.setDomain(domain);
        appUsersDomains.setAppUser(owner);
        appUsersDomains.setDomainRole(domainRoleOptional.get());

        // Step 5: Persist
        domainRepository.save(domain);
        appUsersDomainsRepository.save(appUsersDomains);

        return Output.success(new CreateDomainResult(slug));
    }

    @Transactional(readOnly = true)
    public Output<ListDomainsResult> listDomains(String username) {
        Optional<AppUser> appUserOptional = appUserRepository.findByUsername(username);
        if (appUserOptional.isEmpty()) {
            return Output.failure(ErrorCode.APPUSER_FIND_BY_USERNAME_ERROR);
        }
        AppUser appUser = appUserOptional.get();

        List<AppUsersDomains> appUsersDomains = appUsersDomainsRepository.findByAppUser(appUser);
        List<VisibleDomainDTO> allVisibleDomainsDTO = appUsersDomains.stream().map(aud -> {

            Domain d = aud.getDomain();
            DomainDTO domainDTO = domainMapper.toDTO(new DomainMapper.DomainWithOwner(d, d.getOwner()));
            String selfRole = aud.getDomainRole().getCode();
            return new VisibleDomainDTO(domainDTO, selfRole);
        }).toList();

        var ownedDomains = allVisibleDomainsDTO.stream().filter(
                visDomain -> visDomain.selfDomainRoleCode().equals(DomainRoleEnum.OWNER.name())).toList();
        var visibleDomains = allVisibleDomainsDTO.stream()
                .filter(e -> !ownedDomains.contains(e)).toList();


        ListDomainsResult result = new ListDomainsResult(ownedDomains, visibleDomains);
        return Output.success(result);
    }

    @Transactional(readOnly = true)
    public Output<HasDomainReadAccessResult> assertDomainReadAccess(String owner, String slug, String username) {

        var output = getAppUserDomainRole(owner, slug, username);

        if (output.isFailure()) {
            return output.mapFailure();
        }
        AppUserDomainRoleData data = output.getData().get();
        DomainRole role = data.role();
        Domain domain = data.domain();
        AppUser appUser = data.user();
        DomainRoleEnum roleEnum = DomainRoleEnum.valueOf(role.getCode());
        Boolean hasAccess = hasViewAccess(roleEnum.name());

        if (!hasAccess) {
            return Output.failure(ErrorCode.DOMAIN_NO_VIEW_ACCESS);
        }
        return Output.success(new HasDomainReadAccessResult(hasAccess, roleEnum, domain.getDomainId(), appUser.getAppUserId()));
    }

    private Output<AppUserDomainRoleData> getAppUserDomainRole(String owner, String slug, String username) {

        var domainOutput = getDomain(owner, slug);

        if (domainOutput.isFailure()) {
            return domainOutput.mapFailure();
        }
        Domain domain = domainOutput.getData().get();

        Optional<AppUser> appUserOptional = appUserRepository.findByUsername(username);
        if (appUserOptional.isEmpty()) {
            return Output.failure(ErrorCode.APPUSER_FIND_BY_USERNAME_ERROR, username);
        }

        Optional<AppUsersDomains> roleOptional = appUsersDomainsRepository.findByDomainAndAppUser(domain, appUserOptional.get());
        if (roleOptional.isEmpty()) {
            return Output.failure(ErrorCode.DOMAIN_NO_ROLE_FOR_DOMAIN);
        }
        DomainRole role = roleOptional.get().getDomainRole();
        return Output.success(new AppUserDomainRoleData(appUserOptional.get(), domain, role));
    }

    @Transactional(readOnly = true)
    public Output<DomainDetailResult> getDomainDetail(Long domainId) {
        Optional<Domain> domainOptional = domainRepository.findByDomainId(domainId);

        if (domainOptional.isEmpty()) {
            return Output.failure(ErrorCode.DOMAIN_FIND_BY_DOMAIN_ID_ERROR);
        }
        Domain domain = domainOptional.get();
        // Get Information
        DomainDTO domainDTO = domainMapper.toDTO(new DomainMapper.DomainWithOwner(domain, domain.getOwner()));

        // Get All Roles
        List<AppUsersDomains> appUsersDomains = appUsersDomainsRepository.findByDomain(domain);
        List<AppUserDomainRole> appUserDomainRoles = appUsersDomains
                .stream()
                .map(aud ->
                        new AppUserDomainRole(
                                aud.getAppUser().getUsername(),
                                aud.getDomainRole().getCode()))
                .toList();

        // Get All Platforms
        return Output.success(
                new DomainDetailResult(domainDTO, appUserDomainRoles)
        );
    }

    /**
     * Given a base slug and a list of existing domains, determine the next available slug.
     * Example: baseSlug = "my-domain"
     * existing slugs = ["my-domain", "my-domain-2"]
     * returns "my-domain-3"
     */
    private String nextAvailableSlug(String baseSlug, List<Domain> existingDomains) {
        int max = 0;
        Pattern pattern = Pattern.compile(Pattern.quote(baseSlug) + "-(\\d+)$");

        for (Domain d : existingDomains) {
            String s = d.getSlug();
            if (s.equals(baseSlug)) {
                max = Math.max(max, 1);
            } else {
                Matcher m = pattern.matcher(s);
                if (m.find()) {
                    int n = Integer.parseInt(m.group(1));
                    max = Math.max(max, n);
                }
            }
        }

        return (max == 0) ? baseSlug : baseSlug + "-" + (max + 1);
    }

    private Boolean hasViewAccess(String roleCode) {
        return viewRoles.stream().map(x -> x.name()).toList().contains(roleCode);
    }

    private Output<Domain> getDomain(String owner, String slug) {
        Optional<AppUser> ownerOptional = appUserRepository.findByUsername(owner);
        if (ownerOptional.isEmpty()) {
            return Output.failure(ErrorCode.APPUSER_FIND_BY_USERNAME_ERROR, owner);
        }
        Optional<Domain> domainOptional = domainRepository.findByOwnerAndSlug(ownerOptional.get(), slug);

        if (domainOptional.isEmpty()) {
            return Output.failure(ErrorCode.DOMAIN_FIND_BY_OWNER_SLUG_ERROR, owner + "/" + slug);
        }
        return Output.success(domainOptional.get());
    }


}
