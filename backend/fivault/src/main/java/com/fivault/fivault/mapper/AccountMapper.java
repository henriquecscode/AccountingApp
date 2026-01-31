package com.fivault.fivault.mapper;

import com.fivault.fivault.database.model.Account;
import com.fivault.fivault.dto.AccountDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AccountMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "accountSlug", source = "account.slug")
    @Mapping(target = "accountDescription", source = "account.description")
    AccountDTO toDTO(Account account);
}
