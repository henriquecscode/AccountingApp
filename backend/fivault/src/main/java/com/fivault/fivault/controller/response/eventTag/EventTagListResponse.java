package com.fivault.fivault.controller.response.eventTag;

import com.fivault.fivault.dto.EventTagDTO;

import java.util.List;

public record EventTagListResponse(List<EventTagDTO> eventTagDTOList) {
}
