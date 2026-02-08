package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.account.AccountCreateRequest;
import com.fivault.fivault.controller.request.eventTag.EventTagCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.account.AccountCreateResponse;
import com.fivault.fivault.controller.response.eventTag.EventTagCreateResponse;
import com.fivault.fivault.controller.response.eventTag.EventTagListResponse;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.EventTagService;
import com.fivault.fivault.service.Output;
import com.fivault.fivault.service.result.domain.DomainAccessResult;
import com.fivault.fivault.service.result.eventTag.EventTagCreateResult;
import com.fivault.fivault.service.result.eventTag.EventTagListResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/domain/{owner}/{domainSlug}/eventTag")
public class EventTagController {
    private final DomainService domainService;
    private final EventTagService eventTagService;

    public EventTagController(DomainService domainService, EventTagService eventTagService) {
        this.domainService = domainService;
        this.eventTagService = eventTagService;
    }

    @GetMapping("/list")
    public ResponseEntity<BasicResponse<EventTagListResponse>> list(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainReadAccess(owner, domainSlug, username);
        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<EventTagListResult> eventTagListResultOutput = eventTagService.list(domainId);

        if(eventTagListResultOutput.isFailure()){
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventTagListResultOutput);
        }
        return ResponseEntity.ok(BasicResponse.success(
                new EventTagListResponse(eventTagListResultOutput.getData().get().eventTagDTOList())
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<EventTagCreateResponse>> create(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @Valid @RequestBody EventTagCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();

        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<EventTagCreateResult> eventTagCreateResultOutput = eventTagService.create(domainId, request.getName(), request.getDescription(), request.getParentEventTagId());
        if (eventTagCreateResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventTagCreateResultOutput);
        }

        EventTagCreateResult eventTagCreateResult = eventTagCreateResultOutput.getData().get();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BasicResponse.success(
                        new EventTagCreateResponse(
                                eventTagCreateResult.eventTag()
                        )
                ));
    }
}
