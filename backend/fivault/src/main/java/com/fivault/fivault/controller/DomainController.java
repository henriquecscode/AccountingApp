package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.domain.DomainCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.domain.DomainCreateResponse;
import com.fivault.fivault.controller.response.domain.DomainDetailResponse;
import com.fivault.fivault.controller.response.domain.DomainListResponse;
import com.fivault.fivault.dto.DomainRoleEnum;
import com.fivault.fivault.dto.PlatformDTO;
import com.fivault.fivault.service.AppUserService;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.PlatformService;
import com.fivault.fivault.service.result.Domain.CreateDomainResult;
import com.fivault.fivault.service.result.Domain.HasDomainReadAccessResult;
import com.fivault.fivault.service.result.Domain.ListDomainsResult;
import com.fivault.fivault.service.result.Domain.DomainDetailResult;
import com.fivault.fivault.service.Output;
import com.fivault.fivault.service.result.Platform.PlatformListResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/domain")
public class DomainController {
    private final AppUserService appUserService;
    private final DomainService domainService;
    private final PlatformService platformService;


    public DomainController(DomainService domainService, AppUserService appUserService, PlatformService platformService) {
        this.domainService = domainService;
        this.appUserService = appUserService;
        this.platformService = platformService;
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<DomainCreateResponse>> create(
            @Valid @RequestBody DomainCreateRequest request,
            HttpServletRequest httpRequest
    ) {

        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<CreateDomainResult> output = domainService.createDomain(username, request.getDomainName(), request.getDescription());

        if (output.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(
                    httpRequest,
                    output
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BasicResponse.success(
                        new DomainCreateResponse(output.getData().get().domainSlug())
                ));
    }

    @GetMapping("/list")
    public ResponseEntity<BasicResponse<DomainListResponse>> list(
            HttpServletRequest httpRequest
    ) {

        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<ListDomainsResult> output = domainService.listDomains(username);

        if (output.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(
                    httpRequest,
                    output
            );
        }

        return ResponseEntity.ok(BasicResponse.success(
                DomainListResponse.from(output.getData().get())
        ));
    }

    @GetMapping("/{owner}/{slug}")
    public ResponseEntity<BasicResponse<DomainDetailResponse>> detail(
            @PathVariable String owner,
            @PathVariable String slug,
            HttpServletRequest httpRequest
    ) {

        // Access
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<HasDomainReadAccessResult> output = domainService.assertDomainReadAccess(owner, slug, username);
        if (output.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, output);
        }
        // Logic
        Long domainId = output.getData().get().domainId();
        Long appUserId = output.getData().get().appUserId();
        Output<DomainDetailResult> outputDetail = domainService.getDomainDetail(domainId);
        DomainDetailResult result = outputDetail.getData().get();

        // TODO Only get platforms with at least view access
        Output<PlatformListResult> outputPlatformList = platformService.getPlatformList(domainId, appUserId);

        // TODO Shouldn't I still give some data?
        if (outputPlatformList.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, outputPlatformList);
        }

        PlatformListResult resultPlatformList = outputPlatformList.getData().get();
        return ResponseEntity.ok(BasicResponse.success(
                        new DomainDetailResponse(result.domain(), result.domainAppUsers(), resultPlatformList.platformDTOs())
                )
        );
    }
}
