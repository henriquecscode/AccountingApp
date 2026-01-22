package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.domain.DomainCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.domain.DomainCreateResponse;
import com.fivault.fivault.dto.AppUserDTO;
import com.fivault.fivault.service.AppUserService;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.output.AppUser.GetRequestAppUserResult;
import com.fivault.fivault.service.output.Domain.CreateDomainResult;
import com.fivault.fivault.service.output.Output;
import com.fivault.fivault.util.ControllerOutputFailureUtil;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody DomainCreateRequest request,
            HttpServletRequest httpRequest
    ) {

        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<CreateDomainResult> output = domainService.createDomain(username, request.domainName(), request.description());

        if (output.isFailure()) {

        }
        return ResponseEntity.ok(BasicResponse.success(
                new DomainCreateResponse()
        ));
    }
}
