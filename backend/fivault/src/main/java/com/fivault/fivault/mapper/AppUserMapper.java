package com.fivault.fivault.mapper;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.dto.AppUserDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR  // Fail on unmapped fields
)
public interface AppUserMapper {

    AppUserDTO toDTO(AppUser user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "username", source="username")
    @Mapping(target = "email", source="email")
    @Mapping(target = "name", source="name")
    AppUser toEntity(AppUserDTO dto);

    List<AppUserDTO> toDTOList(List<AppUser> users);
}