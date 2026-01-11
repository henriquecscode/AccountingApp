package com.fivault.fivault.service;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUserSession;
import com.fivault.fivault.dto.ActiveDeviceDTO;
import com.fivault.fivault.repository.AppUserRepository;
import com.fivault.fivault.repository.AppUserSessionRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.impl.PasswordService;
import com.fivault.fivault.service.impl.TokenHashService;
import com.fivault.fivault.service.output.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fivault.fivault.util.DeviceFingerprint;
import com.fivault.fivault.util.RandomUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final AppUserSessionRepository appUserSessionRepository;
    private final PasswordService passwordService;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;
    private final DeviceFingerprint deviceFingerprint;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);


    public AuthService(AppUserRepository userRepository,
                       AppUserSessionRepository appUserSessionRepository,
                       PasswordService passwordService, TokenHashService tokenHashService,
                       JwtService jwtService,
                       DeviceFingerprint deviceFingerprint) {
        this.userRepository = userRepository;
        this.appUserSessionRepository = appUserSessionRepository;
        this.passwordService = passwordService;
        this.tokenHashService = tokenHashService;
        this.jwtService = jwtService;
        this.deviceFingerprint = deviceFingerprint;
    }

    @Transactional(rollbackOn = Exception.class)
    public Output<SignUpResult> signUp(String username, String email, String password, HttpServletRequest httpRequest) {
        // Validate input
        if (username == null || username.isBlank()) {
            return Output.failure(ErrorCode.VALIDATION_INVALID_INPUT);
        }
        if (email == null || email.isBlank()) {
            return Output.failure(ErrorCode.VALIDATION_INVALID_INPUT);
        }
        if (password == null || password.isBlank()) {
            return Output.failure(ErrorCode.VALIDATION_INVALID_INPUT);
        }

        if (userRepository.existsByUsername(username)) {
            return Output.failure(ErrorCode.AUTH_USER_EXISTS);
        }
        // Check if email exists. Might drop this constraint?
        if (userRepository.existsByEmail(email.toLowerCase())) {
            return Output.failure(ErrorCode.AUTH_USER_EXISTS);
        }

        boolean strongPassword = passwordService.testPasswordStrength(password);

        if (!strongPassword) {
            return Output.failure(ErrorCode.AUTH_WEAK_PASSWORD);
        }
        // Create new user
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email.toLowerCase());

        // Generate salt and hash password
        String hashedPassword = passwordService.hashPassword(password);

        user.setPasswordHash(hashedPassword);
        user.setActive(true);
        user.setEmailVerified(false);
        user.setFailedLoginAttempts(0);

        user = userRepository.save(user);

        var result = getCreateSession(httpRequest, user);

        return Output.success(new SignUpResult(result.jwtToken(), result.refreshToken(), result.deviceName()));
    }

    public Output<LogInResult> logIn(String username, String password, HttpServletRequest httpRequest) {
        Optional<AppUser> optionalAppUserByUser = userRepository.findByUsername(username);
        if (optionalAppUserByUser.isEmpty()) {
            return Output.failure(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        Optional<AppUser> optionalAppUser = optionalAppUserByUser;
        AppUser user = optionalAppUser.get();
        String hashedPassword = user.getPasswordHash();
        boolean success = passwordService.verifyPassword(password, hashedPassword);

        if (!success) {
            return Output.failure(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        var result = getCreateSession(httpRequest, user);

        return Output.success(new LogInResult(result.jwtToken, result.refreshToken()));
    }

    public Output<RefreshSessionResult> refreshAccessToken(String token, HttpServletRequest httpRequest) {
        // First validate refresh token
        AppUserSession appUserSession = fetchAppUserSession(token);
        if (appUserSession == null) {
            return Output.failure(ErrorCode.AUTH_INVALID_SESSION);
        }
        AppUser appUser = appUserSession.getUser();
        if (appUser == null) {
            return Output.failure(ErrorCode.AUTH_INVALID_SESSION);
        }
        appUserSession.setRevoked(true);
        var result = getCreateSession(httpRequest, appUser);

        return Output.success(new RefreshSessionResult(result.jwtToken, result.refreshToken()));
    }

    public Output<LogoutResult> logout(String token) {
        AppUserSession appUserSession = fetchAppUserSession(token);
        if (appUserSession == null) {
            return Output.failure(ErrorCode.AUTH_INVALID_SESSION);
        }
        AppUser appUser = appUserSession.getUser();
        if (appUser == null) {
            return Output.failure(ErrorCode.AUTH_INVALID_SESSION);
        }
        appUserSession.setRevoked(true);
        return Output.success(new LogoutResult());
    }

    public void logoutAllDevices(Long userId) {
    }

    public List<ActiveDeviceDTO> getActiveDevices(Long userId) {
        return null;
    }

    public void logoutDevice(Long userId, String deviceId) {
    }

    private AppUserSession createAppUserSession(AppUser user, String deviceId,
                                                String deviceName, String plainToken, String ipAddress, String userAgent) {
        AppUserSession token = new AppUserSession();

        // Generate salt and hash the token before storing
        String tokenHash = tokenHashService.hashData(plainToken);

        token.setTokenHash(tokenHash);
        token.setUser(user);
        token.setDeviceId(deviceId);
        token.setDeviceName(deviceName);
        token.setIpAddress(ipAddress);
        token.setUserAgent(userAgent);
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setLastUsedAt(LocalDateTime.now());
        token.setRevoked(false);
        return appUserSessionRepository.save(token);
    }

    private record UserSessionAuthCredentials(String deviceName, String refreshToken, String jwtToken) {
    }

    private UserSessionAuthCredentials getCreateSession(HttpServletRequest httpRequest, AppUser user) {
        // Generate device info
        String deviceId = deviceFingerprint.generateDeviceId(httpRequest);
        String deviceName = deviceFingerprint.extractDeviceName(httpRequest.getHeader("User-Agent"));

        // Generate plain refresh token (this goes to the user)
        String refreshToken = RandomUtil.randomBase64Url(64);

        // Create and save refresh token in database (hashed)
        createAppUserSession(user, deviceId, deviceName, refreshToken, httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user.getUsername(), user.getAppUserId());
        UserSessionAuthCredentials result = new UserSessionAuthCredentials(deviceName, refreshToken, jwtToken);
        return result;
    }

    private AppUserSession fetchAppUserSession(String token) {

        String hashToken = tokenHashService.hashData(token);
        Optional<AppUserSession> sessionInfoOptional = appUserSessionRepository.findByTokenHashAndRevokedFalse(hashToken);
        if (sessionInfoOptional.isEmpty()) {
            return null;
        }
        AppUserSession appUserSession = sessionInfoOptional.get();
        if (appUserSession.isExpired()) {
            return null;
        }
        return appUserSession;

    }

}
