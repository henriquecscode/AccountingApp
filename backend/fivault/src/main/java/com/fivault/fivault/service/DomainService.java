package com.fivault.fivault.service;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUsersDomains;
import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.DomainRole;
import com.fivault.fivault.dto.DomainRoleEnum;
import com.fivault.fivault.dto.VisibleDomainDTO;
import com.fivault.fivault.repository.AppUserRepository;
import com.fivault.fivault.repository.AppUsersDomainsRepository;
import com.fivault.fivault.repository.DomainRepository;
import com.fivault.fivault.repository.DomainRoleRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.output.Domain.CreateDomainResult;
import com.fivault.fivault.service.output.Domain.GetDomainsResult;
import com.fivault.fivault.service.output.Domain.ListDomainsResult;
import com.fivault.fivault.service.output.Output;
import com.fivault.fivault.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DomainService {
    private final AppUserRepository appUserRepository;
    private final DomainRepository domainRepository;
    private final AppUsersDomainsRepository appUsersDomainsRepository;
    private final DomainRoleRepository domainRoleRepository;

    public DomainService(AppUserRepository appUserRepository, DomainRepository domainRepository, AppUsersDomainsRepository appUsersDomainsRepository, DomainRoleRepository domainRoleRepository) {
        this.appUserRepository = appUserRepository;
        this.domainRepository = domainRepository;
        this.appUsersDomainsRepository = appUsersDomainsRepository;
        this.domainRoleRepository = domainRoleRepository;
    }

    public Output<GetDomainsResult> getDomains(Long appUserId) {


        return Output.success(new GetDomainsResult());
    }

    @Transactional
    public Output<CreateDomainResult> createDomain(String ownerUsername, String name, String description) {
        // Step 1: Generate base slug
        Optional<AppUser> ownerOptional = appUserRepository.findByUsername(ownerUsername);
        if (ownerOptional.isEmpty()) {
            return Output.failure(ErrorCode.FIND_BY_USERNAME_ERROR);
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

    @Transactional
    public Output<ListDomainsResult> listDomains(String username) {
        Optional<AppUser> appUserOptional = appUserRepository.findByUsername(username);
        if (appUserOptional.isEmpty()) {
            return Output.failure(ErrorCode.FIND_BY_USERNAME_ERROR);
        }
        AppUser appUser = appUserOptional.get();

        List<AppUsersDomains> appUsersDomains = appUsersDomainsRepository.findByAppUser(appUser);
        List<VisibleDomainDTO> allVisibleDomainsDTO = appUsersDomains.stream().map(aud -> {
            Domain d = aud.getDomain();
            String selfRole = aud.getDomainRole().getCode();
            String owner = d.getOwner().getUsername();
            return new VisibleDomainDTO(owner, d.getName(), d.getSlug(), d.getDescription(), selfRole);
        }).toList();

        var ownedDomains = allVisibleDomainsDTO.stream().filter(
                visDomain -> visDomain.selfDomainRoleCode().equals(DomainRoleEnum.OWNER.name())).toList();
        var visibleDomains = allVisibleDomainsDTO.stream()
                .filter(e -> !ownedDomains.contains(e)).toList();


        ListDomainsResult result = new ListDomainsResult(ownedDomains, visibleDomains);
        return Output.success(result);
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

}
