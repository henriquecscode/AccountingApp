package com.fivault.fivault.service;

import com.fivault.fivault.database.model.*;
import com.fivault.fivault.dto.PlatformDTO;
import com.fivault.fivault.mapper.PlatformMapper;
import com.fivault.fivault.repository.DomainRepository;
import com.fivault.fivault.repository.PlatformRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.domain.DomainAccessResult;
import com.fivault.fivault.service.result.platform.*;
import com.fivault.fivault.util.SlugUtil;
import com.fivault.fivault.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    private final DomainService domainService;
    private final DomainRepository domainRepository;
    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;

    public PlatformService(DomainService domainService, DomainRepository domainRepository, PlatformRepository platformRepository, PlatformMapper platformMapper) {
        this.domainService = domainService;
        this.domainRepository = domainRepository;
        this.platformRepository = platformRepository;
        this.platformMapper = platformMapper;
    }


    @Transactional(readOnly = false)
    public Output<PlatformCreateResult> createPlatform(Long domainId, String platformName, String description) {
        // Step 1: Generate base slug

        // TODO change to Entity Manager reference
        Optional<Domain> domainOptional = domainRepository.findByDomainId(domainId);

        if (domainOptional.isEmpty()) {
            return Output.failure(ErrorCode.DOMAIN_FIND_BY_DOMAIN_ID_ERROR);
        }

        Domain domain = domainOptional.get();

        if (platformName == null || platformName.trim().isEmpty()) {
            return Output.failure(ErrorCode.PLATFORM_CREATE_NO_NAME);
        }

        String baseSlug = SlugUtil.generateSlug(platformName);

        if (baseSlug == null || baseSlug.isBlank()) {
            return Output.failure(ErrorCode.PLATFORM_CREATE_INVALID_SLUG);
        }

        if (StringUtil.isValidUUID(baseSlug)) {
            return Output.failure(ErrorCode.PLATFORM_CREATE_INVALID_SLUG_UUID);
        }

        // Step 2: Fetch all slugs that start with this base for the domain
        List<Platform> existingPlatforms = platformRepository.findByDomainAndSlugStartingWith(domain, baseSlug);

        // Step 3: Determine next available slug
        String slug = nextAvailableSlug(baseSlug, existingPlatforms);

        // Step 4: Create the platform
        Platform platform = new Platform();
        platform.setName(platformName);
        platform.setSlug(slug);
        platform.setDomain(domain);
        platform.setDescription(description);

        // Step 5: Persist
        platformRepository.save(platform);

        return Output.success(new PlatformCreateResult(slug));
    }

    @Transactional(readOnly = true)
    public Output<PlatformListResult> getPlatformList(Long domainId, Long user) {

        Optional<Domain> domainOptional = domainRepository.findByDomainId(domainId);

        if (domainOptional.isEmpty()) {
            return Output.failure(ErrorCode.DOMAIN_FIND_BY_DOMAIN_ID_ERROR);
        }
        // TODO use UserId to get only platforms with permission
        List<Platform> platforms = platformRepository.findByDomain(domainOptional.get());
        List<PlatformDTO> platformDTOS = platforms.stream()
                .map(platformMapper::toDTO).toList();

        return Output.success(new PlatformListResult(platformDTOS));
    }

    @Transactional(readOnly = true)
    public Output<PlatformDetailResult> getPlatformDetail(Long platformId) {
        Optional<Platform> platformOptional = platformRepository.findByPlatformId(platformId);

        if (platformOptional.isEmpty()) {
            return Output.failure(ErrorCode.PLATFORM_FIND_BY_PLATFORM_ID_ERROR);
        }
        Platform platform = platformOptional.get();
        // Get Information
        PlatformDTO platformDTO = platformMapper.toDTO(platform);

        return Output.success(
                new PlatformDetailResult(platformDTO)
        );
    }

    @Transactional(readOnly = true)
    public Output<PlatformAccessResult> assertPlatformReadAccess(String owner, String slug, String username, String platformSlug) {
        // First must have read of domain
        Output<DomainAccessResult> accessResultOutput = domainService.assertDomainReadAccess(owner, slug, username);

        if (accessResultOutput.isFailure()) {
            return accessResultOutput.mapFailure();
        }
        DomainAccessResult result = accessResultOutput.getData().get();

        Long domainId = result.domainId();
        Long appUserId = result.appUserId();
        // TODO
        // Still have no feature for specific platform access
        Optional<Platform> optionalPlatform = platformRepository.findByDomain_DomainIdAndSlug(domainId, platformSlug);

        if (optionalPlatform.isEmpty()) {
            return Output.failure(ErrorCode.PLATFORM_FIND_BY_DOMAIN_SLUG_ERROR);
        }

        boolean hasAccess = true;
        if (!hasAccess) {
            return Output.failure(ErrorCode.PLATFORM_NO_READ_ACCESS);
        }
        return Output.success(new PlatformAccessResult(hasAccess, optionalPlatform.get().getPlatformId(), domainId, appUserId));
    }

    @Transactional(readOnly = true)
    public Output<PlatformAccessResult> assertPlatformAdminAccess(String owner, String slug, String username, String platformSlug) {
        // First must have admin of domain
        Output<DomainAccessResult> accessResultOutput = domainService.assertDomainAdminAccess(owner, slug, username);

        if (accessResultOutput.isFailure()) {
            return accessResultOutput.mapFailure();
        }
        DomainAccessResult result = accessResultOutput.getData().get();

        Long domainId = result.domainId();
        Long appUserId = result.appUserId();
        // TODO
        // Still have no feature for specific platform access
        Optional<Platform> optionalPlatform = platformRepository.findByDomain_DomainIdAndSlug(domainId, platformSlug);

        if (optionalPlatform.isEmpty()) {
            return Output.failure(ErrorCode.PLATFORM_FIND_BY_DOMAIN_SLUG_ERROR);
        }

        boolean hasAccess = true;
        if (!hasAccess) {
            return Output.failure(ErrorCode.PLATFORM_NO_ADMIN_ACCESS);
        }
        return Output.success(new PlatformAccessResult(hasAccess, optionalPlatform.get().getPlatformId(), domainId, appUserId));
    }

    /**
     * Given a base slug and a list of existing platforms, determine the next available slug.
     * Example: baseSlug = "my-domain"
     * existing slugs = ["my-domain", "my-domain-2"]
     * returns "my-domain-3"
     */
    private String nextAvailableSlug(String baseSlug, List<Platform> existingPlatforms) {
        return SlugUtil.nextAvailableSlug(
                baseSlug,
                existingPlatforms.stream().map(
                        Platform::getSlug
                ).toList()
        );
    }
}
