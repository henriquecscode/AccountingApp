package com.fivault.fivault.service;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.dto.AppUserDTO;
import com.fivault.fivault.mapper.AppUserMapper;
import com.fivault.fivault.repository.AppUserRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.AppUser.GetRequestAppUserResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }


    /**
     * @return Information on the user from http request jwtToken.
     */
    public Output<GetRequestAppUserResult> getRequestAppUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String username = userDetails.getUsername();
            AppUser appUser = appUserRepository.findByUsername(username).orElseThrow();
            AppUserDTO appUserDTO = appUserMapper.toDTO(appUser);
            return Output.success(new GetRequestAppUserResult(appUserDTO));
        } catch (Exception e) {
            return Output.failure(ErrorCode.APPUSER_FAILURE_FETCHING_APPUSER);
        }
    }
}
