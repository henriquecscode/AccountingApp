package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.account.AccountCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.account.AccountCreateResponse;
import com.fivault.fivault.controller.response.account.AccountDetailResponse;
import com.fivault.fivault.service.*;
import com.fivault.fivault.service.result.Account.AccountAccessResult;
import com.fivault.fivault.service.result.Account.AccountDetailResult;
import com.fivault.fivault.service.result.Platform.PlatformAccessResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/domain/{owner}/{domainSlug}/platform/{platformSlug}/account")
public class AccountController {
    private final PlatformService platformService;
    private final AccountService accountService;

    public AccountController(PlatformService platformService, AccountService accountService) {
        this.platformService = platformService;
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<AccountCreateResponse>> create(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @PathVariable String platformSlug,
            @Valid @RequestBody AccountCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        // Access
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<PlatformAccessResult> platformAccessOutput = platformService.assertPlatformAdminAccess(owner, domainSlug, username, platformSlug);

        if (platformAccessOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, platformAccessOutput);
        }

        Long platformId = platformAccessOutput.getData().get().platformid();


        var output = accountService.createAccount(platformId, request.getAccountName(), request.getDescription());

        if (output.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(
                    httpRequest,
                    output
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BasicResponse.success(
                        new AccountCreateResponse(output.getData().get().accountSlug())
                ));
    }

    @GetMapping("/{accountSlug}")
    public ResponseEntity<BasicResponse<AccountDetailResponse>> detail(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @PathVariable String platformSlug,
            @PathVariable String accountSlug,
            HttpServletRequest httpRequest
    ) {

        // Access
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<AccountAccessResult> accessoutput = accountService.assertAccountReadAccess(username, owner, domainSlug, platformSlug, accountSlug);
        if (accessoutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, accessoutput);
        }

        AccountAccessResult accessResult = accessoutput.getData().get();
        Long platformId = accessResult.platformId();

        // Logic
        Output<AccountDetailResult> outputDetail = accountService.getAccountDetail(platformId);

        if (outputDetail.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, outputDetail);
        }

        AccountDetailResult detailResult = outputDetail.getData().get();
        return ResponseEntity.ok(BasicResponse.success(
                        new AccountDetailResponse(detailResult.accountDTO())
                )
        );
    }
}
