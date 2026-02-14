package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.eventCategory.EventCategoryCreateUpdateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.eventCategory.EventCategoryCreateUpdateResponse;
import com.fivault.fivault.controller.response.eventCategory.EventCategoryListResponse;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.EventCategoryService;
import com.fivault.fivault.service.Output;
import com.fivault.fivault.service.result.domain.DomainAccessResult;
import com.fivault.fivault.service.result.eventCategory.EventCategoryCreateUpdateResult;
import com.fivault.fivault.service.result.eventCategory.EventCategoryListResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/domain/{owner}/{domainSlug}/eventCategory")
public class EventCategoryController {
    private final DomainService domainService;
    private final EventCategoryService eventCategoryService;

    public EventCategoryController(DomainService domainService, EventCategoryService eventCategoryService) {
        this.domainService = domainService;
        this.eventCategoryService = eventCategoryService;
    }

    @GetMapping("/list")
    public ResponseEntity<BasicResponse<EventCategoryListResponse>> list(
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

        Output<EventCategoryListResult> eventCategoryListResultOutput = eventCategoryService.list(domainId);

        if (eventCategoryListResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventCategoryListResultOutput);
        }
        return ResponseEntity.ok(BasicResponse.success(
                new EventCategoryListResponse(eventCategoryListResultOutput.getData().get().eventCategoryDTOList())
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<EventCategoryCreateUpdateResponse>> create(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @Valid @RequestBody EventCategoryCreateUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();

        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<EventCategoryCreateUpdateResult> eventCategoryCreateResultOutput = eventCategoryService.create(domainId, request.getName(), request.getDescription(), request.getParentEventCategoryId());
        if (eventCategoryCreateResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventCategoryCreateResultOutput);
        }

        EventCategoryCreateUpdateResult eventCategoryCreateUpdateResult = eventCategoryCreateResultOutput.getData().get();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BasicResponse.success(
                        new EventCategoryCreateUpdateResponse(
                                eventCategoryCreateUpdateResult.eventCategoryDTO()
                        )
                ));
    }

    @PostMapping("/update/{eventCategoryId}")
    public ResponseEntity<BasicResponse<EventCategoryCreateUpdateResponse>> update(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @PathVariable UUID eventCategoryId,
            @Valid @RequestBody EventCategoryCreateUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();

        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<EventCategoryCreateUpdateResult> eventCategoryCreateResultOutput = eventCategoryService.update(domainId, eventCategoryId, request.getName(), request.getDescription(), request.getParentEventCategoryId());
        if (eventCategoryCreateResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventCategoryCreateResultOutput);
        }

        EventCategoryCreateUpdateResult eventCategoryCreateUpdateResult = eventCategoryCreateResultOutput.getData().get();
        return ResponseEntity.ok(
                BasicResponse.success(
                        new EventCategoryCreateUpdateResponse(
                                eventCategoryCreateUpdateResult.eventCategoryDTO()
                        )
                ));
    }

    @DeleteMapping("/delete/{eventCategoryId}")
    public ResponseEntity<BasicResponse<Void>> delete(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @PathVariable UUID eventCategoryId,
            HttpServletRequest httpRequest
    ) {
        String username = SecurityUtil.GetRequestAppUserUsername();

        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainAdminAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();

        Output<Void> eventCategoryCreateResultOutput = eventCategoryService.delete(eventCategoryId);
        if (eventCategoryCreateResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, eventCategoryCreateResultOutput);
        }

        return ResponseEntity.ok(BasicResponse.success(null));
    }
}
