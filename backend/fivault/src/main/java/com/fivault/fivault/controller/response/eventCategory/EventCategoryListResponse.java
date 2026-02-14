package com.fivault.fivault.controller.response.eventCategory;

import com.fivault.fivault.dto.EventCategoryDTO;

import java.util.List;

public record EventCategoryListResponse(List<EventCategoryDTO> eventCategoryDTOList) {
}
