package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.event.EventCreateRequest;
import com.fivault.fivault.controller.request.platform.PlatformCreateRequest;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.controller.response.event.EventCreateResponse;
import com.fivault.fivault.service.DomainService;
import com.fivault.fivault.service.EventService;
import com.fivault.fivault.service.Output;
import com.fivault.fivault.service.params.event.EventCreateParams;
import com.fivault.fivault.service.result.domain.DomainAccessResult;
import com.fivault.fivault.service.result.event.EventCreateResult;
import com.fivault.fivault.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/domain/{owner}/{domainSlug}/event")
public class EventController {

    private final DomainService domainService;
    private final EventService eventService;

    public EventController(DomainService domainService, EventService eventService) {
        this.domainService = domainService;
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<EventCreateResponse>> create(
            @PathVariable String owner,
            @PathVariable String domainSlug,
            @Valid @RequestBody EventCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        // Access
        String username = SecurityUtil.GetRequestAppUserUsername();
        Output<DomainAccessResult> domainAccessResultOutput = domainService.assertDomainMemberAccess(owner, domainSlug, username);

        if (domainAccessResultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, domainAccessResultOutput);
        }

        // Logic
        DomainAccessResult result = domainAccessResultOutput.getData().get();
        Long domainId = result.domainId();
        Output<EventCreateResult> resultOutput = eventService.createEvent(new EventCreateParams(domainId, request.getTitle(), request.getDescription(), request.getStartTimestamp(), request.getEndTimestamp()));

        if (resultOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, resultOutput);
        }

        return ResponseEntity.ok(BasicResponse.success(
                new EventCreateResponse(resultOutput.getData().get().eventId())
        ));
    }
}
