package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.platform.PlatformCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.platform.PlatformCreateResponse;
import com.fivault.fivault.controller.response.platform.PlatformDetailResponse;
import com.fivault.fivault.controller.response.platform.PlatformListResponse;
import com.fivault.fivault.service.*;
import com.fivault.fivault.service.result.account.AccountListResult;
import com.fivault.fivault.service.result.domain.DomainAccessResult;
import com.fivault.fivault.service.result.platform.PlatformAccessResult;
import com.fivault.fivault.service.result.platform.PlatformCreateResult;
import com.fivault.fivault.service.result.platform.PlatformDetailResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/domain/{owner}/{domainSlug}/platform")
public class PlatformController {

    private final DomainService domainService;
    private final AppUserService appUserService;
    private final PlatformService platformService;
    private final AccountService accountService;

    public PlatformController(DomainService domainService, AppUserService appUserService, PlatformService platformService, AccountService accountService) {
        this.domainService = domainService;
        this.appUserService = appUserService;
        this.platformService = platformService;
        this.accountService = accountService;
    }

    @GetMapping("/{platformSlug}")
    public ResponseEntity<BasicResponse<PlatformDetailResponse>> detail(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @PathVariable String platformSlug,
            HttpServletRequest httpRequest
    ) {

        // Access
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<PlatformAccessResult> platformAccessOutput = platformService.assertPlatformReadAccess(owner, domainSlug, username, platformSlug);

        if (platformAccessOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, platformAccessOutput);
        }

        // Logic
        PlatformAccessResult result = platformAccessOutput.getData().get();
        Long platformId = result.platformid();
        Long appUserId = result.appUserId();
        Output<PlatformDetailResult> outputDetail = platformService.getPlatformDetail(platformId);

        if (outputDetail.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, outputDetail);
        }

        PlatformDetailResult detailResult = outputDetail.getData().get();

        // TODO Only get accounts with at least view access
        Output<AccountListResult> outputAccountList = accountService.getAccountList(platformId, appUserId);

        if (outputAccountList.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, outputAccountList);
        }

        AccountListResult resultAccountList = outputAccountList.getData().get();
        return ResponseEntity.ok(BasicResponse.success(
                        new PlatformDetailResponse(detailResult.platformDTO(), resultAccountList.accountDTOs())
                )
        );
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<PlatformCreateResponse>> createEvent(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @Valid @RequestBody PlatformCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        // Access
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<DomainAccessResult> domainAccessOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessOutput);
        }
        DomainAccessResult result = domainAccessOutput.getData().get();
        Long domainId = result.domainId();
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
    public ResponseEntity<BasicResponse<PlatformListResponse>> list(
            @PathVariable String owner,
            @PathVariable String domainSlug,
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
