package com.fivault.fivault.service;

import com.fivault.fivault.database.model.AppUser;
import com.fivault.fivault.database.model.AppUserSession;
import com.fivault.fivault.dto.ActiveDeviceDTO;
import com.fivault.fivault.dto.response.AuthResponse;
import com.fivault.fivault.exception.SignupException;
import com.fivault.fivault.exception.UserAlreadyExistsException;
import com.fivault.fivault.repository.AppUserRepository;
import com.fivault.fivault.repository.AppUserSessionRepository;
import com.fivault.fivault.service.impl.PasswordService;
import com.fivault.fivault.service.impl.TokenHashService;
import com.fivault.fivault.service.model.SignUpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional
    public SignUpResponse signUp(String email, String password, HttpServletRequest httpRequest) {
        try {
            // Validate input
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password must be at least 8 characters");
            }

            // Check if user exists
            if (userRepository.existsByEmail(email.toLowerCase())) {
                throw new IllegalArgumentException("Email already registered");
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

            // Generate device info
            String deviceId = deviceFingerprint.generateDeviceId(httpRequest);
            String deviceName = deviceFingerprint.extractDeviceName(httpRequest.getHeader("User-Agent"));

            // Generate plain refresh token (this goes to the user)
            String refreshToken = RandomUtil.randomBase64Url(64);

            // Create and save refresh token in database (hashed)
            createAppUserSession(user, deviceId, deviceName, refreshToken, httpRequest);

            // Generate JWT token
            String jwtToken = jwtService.generateToken(user.getEmail(), user.getAppUserId());

            return new SignUpResponse(jwtToken, refreshToken, deviceName);
        } catch (UserAlreadyExistsException e) {
            // Expected business exception - rethrow
            logger.warn("Signup attempt with existing email: {}", email);
            throw e;
        } catch (DataIntegrityViolationException e) {
            // Database constraint violation
            logger.error("Data integrity violation during signup: {}", e.getMessage());
            throw new SignupException("Unable to create account due to data conflict", e);

        } catch (DataAccessException e) {
            // General database errors
            logger.error("Database error during signup: {}", e.getMessage(), e);
            throw new SignupException("Unable to create account due to system error", e);

        } catch (Exception e) {
            // Unexpected errors
            logger.error("Unexpected error during signup: {}", e.getMessage(), e);
            throw new SignupException("An unexpected error occurred during signup", e);
        }
    }


    public AuthResponse signIn(String email, String password, HttpServletRequest httpRequest) {
        return null;
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
                                                String deviceName, String plainToken,
                                                HttpServletRequest request) {
        AppUserSession token = new AppUserSession();

        // Generate salt and hash the token before storing
        String tokenSalt = tokenHashService.generateSalt();
        String tokenHash = tokenHashService.hashData(plainToken, tokenSalt);

        token.setTokenHash(tokenHash);
        token.setTokenSalt(tokenSalt);
        token.setUser(user);
        token.setDeviceId(deviceId);
        token.setDeviceName(deviceName);
        token.setIpAddress(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setLastUsedAt(LocalDateTime.now());
        token.setRevoked(false);
        return appUserSessionRepository.save(token);
    }

}
