package com.fivault.fivault.service.result.eventCategory;

import com.fivault.fivault.dto.EventCategoryDTO;

import java.util.List;

public record EventCategoryListResult(List<EventCategoryDTO> eventCategoryDTOList) {
}
