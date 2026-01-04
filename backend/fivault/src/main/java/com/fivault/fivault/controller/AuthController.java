package com.fivault.fivault.controller;

import com.fivault.fivault.dto.*;
import com.fivault.fivault.dto.request.RefreshRequest;
import com.fivault.fivault.dto.request.SignInRequest;
import com.fivault.fivault.dto.request.SignUpRequest;
import com.fivault.fivault.dto.response.AuthResponse;
import com.fivault.fivault.dto.response.BasicResponse;
import com.fivault.fivault.dto.response.TokenVerificationResponse;
import com.fivault.fivault.service.AuthService;
import com.fivault.fivault.service.JwtService;
import com.fivault.fivault.service.model.SignUpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fivault.fivault.util.CookieUtil;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;


    public AuthController(AuthService authService, JwtService jwtService, CookieUtil cookieUtil) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping("/try-authenticated")
    public String hi(){
        return "Authenticated";
    }

    @GetMapping("/try-not-authenticated")
    public String hiNotauth(){
        return "Not Authenticated";
    }

    /**
     * Sign up a new user
     * POST /api/auth/signup
     * Body: { "email": "user@example.com", "password": "password123" }
     */
    @PostMapping("/signup")
    public ResponseEntity<BasicResponse<AuthResponse>> signUp(
            @RequestBody SignUpRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        try {
            SignUpResponse signUpResponse = authService.signUp(
                    request.email(),
                    request.password(),
                    httpRequest
            );

            // Create and add refresh token cookie
            httpResponse.addCookie(
                    cookieUtil.createRefreshTokenCookie(signUpResponse.refreshToken())
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new BasicResponse<>(new AuthResponse(signUpResponse.accessToken(), null, signUpResponse.deviceName()))
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BasicResponse<>(false, null, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BasicResponse<>(false, null, null, null));
        }
    }

    /**
     * Sign in an existing user
     * POST /api/auth/signin
     * Body: { "email": "user@example.com", "password": "password123" }
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(
            @RequestBody SignInRequest request,
            HttpServletRequest httpRequest
    ) {
        try {
            AuthResponse response = authService.signIn(
                    request.email(),
                    request.password(),
                    httpRequest
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(null, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, e.getMessage(), null));
        }
    }

    /**
     * Refresh access token using refresh token
     * POST /api/auth/refresh
     * Body: { "refreshToken": "your-refresh-token" }
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest) {
        try {
            String newAccessToken = authService.refreshAccessToken(
                    request.refreshToken(),
                    httpRequest
            );
            return ResponseEntity.ok(
                    new AuthResponse(newAccessToken,  "Token refreshed successfully", null)
            );
        } catch (IllegalArgumentException e) {
            // Invalid or expired refresh token
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse( null, e.getMessage(), null));
        }
    }

    /**
     * Logout from current device
     * POST /api/auth/logout
     * Body: { "refreshToken": "your-refresh-token" }
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(
                new AuthResponse( null, "Logged out successfully", null)
        );
    }

    /**
     * Logout from all devices
     * POST /api/auth/logout-all
     * Headers: Authorization: Bearer <access-token>
     */
    @PostMapping("/logout-all")
    public ResponseEntity<AuthResponse> logoutAllDevices(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>"
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);

            authService.logoutAllDevices(userId);
            return ResponseEntity.ok(
                    new AuthResponse( null, "Logged out from all devices successfully", null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse( null, "Invalid token", null));
        }
    }

    /**
     * Get list of active devices/sessions for current user
     * GET /api/auth/devices
     * Headers: Authorization: Bearer <access-token>
     */
    @GetMapping("/devices")
    public ResponseEntity<BasicResponse<List<ActiveDeviceDTO>>> getActiveDevices(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);

            List<ActiveDeviceDTO> devices = authService.getActiveDevices(userId);
            return ResponseEntity.ok(new BasicResponse<>(devices));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BasicResponse<>(false, null, "Unauthorized", null));
        }
    }

    /**
     * Logout a specific device
     * DELETE /api/auth/devices/{deviceId}
     * Headers: Authorization: Bearer <access-token>
     */
    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<AuthResponse> logoutDevice(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String deviceId) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);

            authService.logoutDevice(userId, deviceId);
            return ResponseEntity.ok(
                    new AuthResponse( null, "Device logged out successfully", null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse( null, "Invalid token", null));
        }
    }

    /**
     * Verify if access token is valid (optional endpoint for frontend)
     * GET /api/auth/verify
     * Headers: Authorization: Bearer <access-token>
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);

            if (jwtService.isTokenValid(token)) {
                Long userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);

                return ResponseEntity.ok(new TokenVerificationResponse(
                        true,
                        userId,
                        email,
                        "Token is valid"
                ));
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenVerificationResponse(false, null, null, "Token is invalid"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenVerificationResponse(false, null, null, "Token is invalid"));
        }
    }

    /**
     * Helper method to extract JWT token from Authorization header
     * Handles "Bearer <token>" format
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }
        return authHeader.substring(7); // Remove "Bearer " prefix
    }
}
