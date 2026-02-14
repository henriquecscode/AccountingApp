package com.fivault.fivault.mapper;

import com.fivault.fivault.database.model.EventCategory;
import com.fivault.fivault.dto.EventCategoryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EventCategoryMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "eventCategoryId", source = "eventCategory.eventCategoryId")
    @Mapping(target = "name", source = "eventCategory.name")
    @Mapping(target = "description", source = "eventCategory.description")
    @Mapping(target = "parentEventCategoryId", source = "eventCategory.parentEventCategory.eventCategoryId")
    EventCategoryDTO toDTO(EventCategory eventCategory);
}
