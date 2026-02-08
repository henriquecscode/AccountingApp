package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.eventTag.EventTagCreateUpdateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.eventTag.EventTagCreateUpdateResponse;
import com.fivault.fivault.controller.response.eventTag.EventTagListResponse;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.EventTagService;
import com.fivault.fivault.service.Output;
import com.fivault.fivault.service.result.domain.DomainAccessResult;
import com.fivault.fivault.service.result.eventTag.EventTagCreateUpdateResult;
import com.fivault.fivault.service.result.eventTag.EventTagListResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

        if (eventTagListResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventTagListResultOutput);
        }
        return ResponseEntity.ok(BasicResponse.success(
                new EventTagListResponse(eventTagListResultOutput.getData().get().eventTagDTOList())
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<EventTagCreateUpdateResponse>> create(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @Valid @RequestBody EventTagCreateUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();

        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<EventTagCreateUpdateResult> eventTagCreateResultOutput = eventTagService.create(domainId, request.getName(), request.getDescription(), request.getParentEventTagId());
        if (eventTagCreateResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventTagCreateResultOutput);
        }

        EventTagCreateUpdateResult eventTagCreateUpdateResult = eventTagCreateResultOutput.getData().get();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BasicResponse.success(
                        new EventTagCreateUpdateResponse(
                                eventTagCreateUpdateResult.eventTag()
                        )
                ));
    }

    @PostMapping("/update/{eventTagId}")
    public ResponseEntity<BasicResponse<EventTagCreateUpdateResponse>> update(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @PathVariable UUID eventTagId,
            @Valid @RequestBody EventTagCreateUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();

        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<EventTagCreateUpdateResult> eventTagCreateResultOutput = eventTagService.update(domainId, eventTagId, request.getName(), request.getDescription(), request.getParentEventTagId());
        if (eventTagCreateResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventTagCreateResultOutput);
        }

        EventTagCreateUpdateResult eventTagCreateUpdateResult = eventTagCreateResultOutput.getData().get();
        return ResponseEntity.ok(
                BasicResponse.success(
                        new EventTagCreateUpdateResponse(
                                eventTagCreateUpdateResult.eventTag()
                        )
                ));
    }

    @DeleteMapping("/delete/{eventTagId}")
    public ResponseEntity<BasicResponse<Void>> delete(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @PathVariable UUID eventTagId,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();

        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<Void> eventTagCreateResultOutput = eventTagService.delete(eventTagId);
        if (eventTagCreateResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventTagCreateResultOutput);
        }

        return ResponseEntity.ok(BasicResponse.success(null));
    }
}
