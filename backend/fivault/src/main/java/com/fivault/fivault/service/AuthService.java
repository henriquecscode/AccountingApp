package com.fivault.fivault.service;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUserSession;
import com.fivault.fivault.dto.ActiveDeviceDTO;
import com.fivault.fivault.repository.AppUserRepository;
import com.fivault.fivault.repository.AppUserSessionRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.impl.PasswordService;
import com.fivault.fivault.service.impl.TokenHashService;
import com.fivault.fivault.service.output.Output;
import com.fivault.fivault.service.output.SignInResult;
import com.fivault.fivault.service.output.SignUpResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fivault.fivault.util.DeviceFingerprint;
import com.fivault.fivault.util.RandomUtil;

import java.time.LocalDateTime;
import java.util.List;

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
    public Output<SignUpResult> signUp(String email, String password, HttpServletRequest httpRequest) {
            // Validate input
            if (email == null || email.isBlank()) {
                return Output.failure(ErrorCode.VALIDATION_INVALID_INPUT);
            }
            if (password == null || password.isBlank()) {
                return Output.failure(ErrorCode.VALIDATION_INVALID_INPUT);
            }

            // Check if user exists
            if (userRepository.existsByEmail(email.toLowerCase())) {
                return Output.failure(ErrorCode.AUTH_USER_EXISTS);
            }

            // Create new user
            AppUser user = new AppUser();
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

    public Output<SignInResult> signIn(String email, String password, HttpServletRequest httpRequest) {
        AppUser user = userRepository.findByEmail(email);
                String hashedPassword = user.getPasswordHash();
            boolean success = passwordService.verifyPassword(password, hashedPassword);

            if(!success){
                return Output.failure(ErrorCode.AUTH_INVALID_CREDENTIALS);
            }
            var result = getCreateSession(httpRequest, user);

            return Output.success(new SignInResult(result.jwtToken, result.refreshToken()));

    }

    public String refreshAccessToken(String s, HttpServletRequest httpRequest) {
        return null;
    }

    public void logout(String s) {
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
        String tokenSalt = tokenHashService.generateSalt();
        String tokenHash = tokenHashService.hashData(plainToken, tokenSalt);

        token.setTokenHash(tokenHash);
        token.setTokenSalt(tokenSalt);
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
        String jwtToken = jwtService.generateToken(user.getEmail(), user.getAppUserId());
        UserSessionAuthCredentials result = new UserSessionAuthCredentials(deviceName, refreshToken, jwtToken);
        return result;
    }

}
