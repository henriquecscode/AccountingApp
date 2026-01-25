package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.domain.DomainCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.domain.DomainCreateResponse;
import com.fivault.fivault.controller.response.domain.DomainDetailResponse;
import com.fivault.fivault.controller.response.domain.DomainListResponse;
import com.fivault.fivault.dto.DomainRoleEnum;
import com.fivault.fivault.service.AppUserService;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.result.Domain.CreateDomainResult;
import com.fivault.fivault.service.result.Domain.HasDomainReadAccessResult;
import com.fivault.fivault.service.result.Domain.ListDomainsResult;
import com.fivault.fivault.service.result.Domain.DomainDetailResult;
import com.fivault.fivault.service.Output;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/domain")
public class DomainController {
    private final DomainService domainService;
    private final AppUserService appUserService;


    public DomainController(DomainService domainService, AppUserService appUserService) {
        this.domainService = domainService;
        this.appUserService = appUserService;
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
        return ResponseEntity.ok(BasicResponse.success(
                new DomainCreateResponse()
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
        Output<DomainDetailResult> outputDetail = domainService.getDomainDetail(output.getData().get().domainId());
        DomainDetailResult result = outputDetail.getData().get();

        return ResponseEntity.ok(BasicResponse.success(
                        new DomainDetailResponse(result.domain(), result.domainAppUsers())
                )
        );
    }
}
