package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.platform.PlatformCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.platform.PlatformCreateResponse;
import com.fivault.fivault.controller.response.platform.PlatformListResponse;
import com.fivault.fivault.service.AppUserService;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.Output;
import com.fivault.fivault.service.PlatformService;
import com.fivault.fivault.service.result.Domain.HasDomainReadAccessResult;
import com.fivault.fivault.service.result.Platform.PlatformCreateResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/domain/{owner}/{slug}/platform")
public class PlatformController {

    private final DomainService domainService;
    private final AppUserService appUserService;
    private final PlatformService platformService;

    public PlatformController(DomainService domainService, AppUserService appUserService, PlatformService platformService) {
        this.domainService = domainService;
        this.appUserService = appUserService;
        this.platformService = platformService;
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<PlatformCreateResponse>> create(
            @PathVariable String owner,
            @PathVariable String slug,
            @Valid @RequestBody PlatformCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        // Access
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<HasDomainReadAccessResult> accessOutput = domainService.assertDomainReadAccess(owner, slug, username);
        if (accessOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, accessOutput);
        }

        Long domainId = accessOutput.getData().get().domainId();
        Output<PlatformCreateResult> output = platformService.createPlatform(domainId, request.getPlatformName(), request.getDescription());

        if (output.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(
                    httpRequest,
                    output
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BasicResponse.success(
                        new PlatformCreateResponse(output.getData().get().platformSlug())
                ));
    }

    @GetMapping("/list")
    public ResponseEntity<BasicResponse<PlatformListResponse>> create(
            @PathVariable String owner,
            @PathVariable String slug,
            HttpServletRequest httpRequest
    ) {

        // TODO
        return ResponseEntity.ok(
                BasicResponse.success(
                        new PlatformListResponse()
                )
        );
    }
}
