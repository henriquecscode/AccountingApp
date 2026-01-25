package com.fivault.fivault.mapper;


import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.dto.DomainDTO;
import com.fivault.fivault.dto.VisibleDomainDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DomainMapper {

    public record DomainWithOwner(Domain domain, AppUser owner) {
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "ownerName", source = "domainWithOwner.owner.username")
    @Mapping(target = "domainName", source = "domainWithOwner.domain.name")
    @Mapping(target = "domainSlug", source = "domainWithOwner.domain.slug")
    @Mapping(target = "domainDescription", source = "domainWithOwner.domain.description")
    DomainDTO toDTO(DomainWithOwner domainWithOwner);
}
