package com.fivault.fivault.service;

import com.fivault.fivault.database.model.*;
import com.fivault.fivault.dto.PlatformDTO;
import com.fivault.fivault.mapper.PlatformMapper;
import com.fivault.fivault.repository.DomainRepository;
import com.fivault.fivault.repository.PlatformRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.Platform.PlatformCreateResult;
import com.fivault.fivault.service.result.Platform.PlatformListResult;
import com.fivault.fivault.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlatformService {

    private final DomainRepository domainRepository;
    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;

    public PlatformService(DomainRepository domainRepository, PlatformRepository platformRepository, PlatformMapper platformMapper) {
        this.domainRepository = domainRepository;
        this.platformRepository = platformRepository;
        this.platformMapper = platformMapper;
    }


    @Transactional(readOnly = false)
    public Output<PlatformCreateResult> createPlatform(Long domainId, String platformName, String description) {
        // Step 1: Generate base slug
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

        // Step 2: Fetch all slugs that start with this base for the domain
        List<Platform> existingPlatforms = platformRepository.findByDomainAndSlugStartingWith(domain, baseSlug);

        // Step 3: Determine next available slug
        String slug = nextAvailableSlug(baseSlug, existingPlatforms);

        // Step 4: Create the domain
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

        // TODO use UserId to get only platforms with permission
        Optional<Domain> domainOptional = domainRepository.findByDomainId(domainId);

        if (domainOptional.isEmpty()) {
            return Output.failure(ErrorCode.DOMAIN_FIND_BY_DOMAIN_ID_ERROR);
        }
        List<Platform> platforms = platformRepository.findByDomain(domainOptional.get());
        List<PlatformDTO> platformDTOS = platforms.stream()
                .map(platformMapper::toDTO).toList();

        return Output.success(new PlatformListResult(platformDTOS));
    }

    /**
     * Given a base slug and a list of existing platforms, determine the next available slug.
     * Example: baseSlug = "my-domain"
     * existing slugs = ["my-domain", "my-domain-2"]
     * returns "my-domain-3"
     */
    private String nextAvailableSlug(String baseSlug, List<Platform> existingPlatforms) {
        int max = 0;
        Pattern pattern = Pattern.compile(Pattern.quote(baseSlug) + "-(\\d+)$");

        for (Platform p : existingPlatforms) {
            String s = p.getSlug();
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
