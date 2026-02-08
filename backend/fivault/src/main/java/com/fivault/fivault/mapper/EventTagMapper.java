package com.fivault.fivault.mapper;

import com.fivault.fivault.database.model.Account;
import com.fivault.fivault.database.model.EventTag;
import com.fivault.fivault.dto.AccountDTO;
import com.fivault.fivault.dto.EventTagDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EventTagMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "eventTagId", source = "eventTag.eventTagId")
    @Mapping(target = "name", source = "eventTag.name")
    @Mapping(target = "description", source = "eventTag.description")
    @Mapping(target = "parentEventTagId", source = "eventTag.parentEventTag.eventTagId")
    EventTagDTO toDTO(EventTag eventTag);
}
