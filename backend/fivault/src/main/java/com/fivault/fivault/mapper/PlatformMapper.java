package com.fivault.fivault.mapper;

import com.fivault.fivault.database.model.Platform;
import com.fivault.fivault.dto.PlatformDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PlatformMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "platformName", source = "platform.name")
    @Mapping(target = "platformSlug", source = "platform.slug")
    @Mapping(target = "platformDescription", source = "platform.description")
    PlatformDTO toDTO(Platform platform);
}
