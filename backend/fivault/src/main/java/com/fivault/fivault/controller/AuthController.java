package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.auth.RefreshRequest;
import com.fivault.fivault.controller.request.auth.LogInRequest;
import com.fivault.fivault.controller.request.auth.SignUpRequest;
import com.fivault.fivault.controller.response.auth.LogoutResponse;
import com.fivault.fivault.controller.response.auth.RefreshResponse;
import com.fivault.fivault.controller.response.auth.SignUpResponse;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.service.AuthService;
import com.fivault.fivault.service.JwtService;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.output.Output;
import com.fivault.fivault.service.output.Auth.SignUpResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.fivault.fivault.util.CookieUtil;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

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

    /**
     * Sign up a new user
     * POST /api/auth/signup
     * Body: { "email": "user@example.com", "password": "password123" }
     */
    @PostMapping("/signup")
    public ResponseEntity<BasicResponse<SignUpResponse>> signUp(
            @RequestBody SignUpRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        Output<SignUpResult> output = authService.signUp(
                request.username(),
                request.email(),
                request.password(),
                httpRequest
        );

        if (output.isFailure()) {
            HttpStatus status = null;
            String detail = null;

            Map<String, Object> params = Collections.emptyMap();
            var errorCode = output.getErrorCode().get();
            if (errorCode.equals(ErrorCode.AUTH_USER_EXISTS)) {
                status = HttpStatus.CONFLICT;
                detail = errorCode.name();
                params = Map.of("username", request.username());

            } else if (errorCode.equals(ErrorCode.VALIDATION_INVALID_INPUT)) {
                status = HttpStatus.BAD_REQUEST;
                detail = errorCode.name();
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                detail = errorCode.name();
            }

            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    status,
                    detail
            );
            problemDetail.setTitle(errorCode.getDefaultMessage());
            problemDetail.setProperty("errorCode", errorCode.getCode());
            problemDetail.setProperty("timestamp", Instant.now());
            problemDetail.setProperty("path", httpRequest.getRequestURI());
            if (!params.isEmpty()) {
                problemDetail.setProperty("params", params);
            }
            return ResponseEntity
                    .status(status.value())
                    .body(BasicResponse.failure(problemDetail));
        }

        SignUpResult result = output.getData().get();
        // Create and add refresh token cookie
        ResponseCookie cookie = cookieUtil.createRefreshTokenCookie(result.refreshToken());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        httpResponse.addCookie(
//                cookieUtil.createRefreshTokenCookie(result.refreshToken())
//        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BasicResponse.success(new SignUpResponse(result.accessToken(), null, result.deviceName()))
        );
    }

    /**
     * Sign in an existing user
     * POST /api/auth/login
     * Body: { "email": "user@example.com", "password": "password123" }
     */
    @PostMapping("/login")
    public ResponseEntity<BasicResponse<SignUpResponse>> logIn(
            @RequestBody LogInRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        var output = authService.logIn(
                request.username(),
                request.password(),
                httpRequest
        );

        if (output.isFailure()) {

            HttpStatus status = null;
            String detail = null;
            Map<String, Object> params = Collections.emptyMap();
            var errorCode = output.getErrorCode().get();

            if (errorCode.equals(ErrorCode.AUTH_INVALID_CREDENTIALS)) {
                status = HttpStatus.UNAUTHORIZED;
                detail = errorCode.name();
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                detail = errorCode.name();
            }

            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    status,
                    detail
            );
            problemDetail.setTitle(errorCode.getDefaultMessage());
            problemDetail.setProperty("errorCode", errorCode.getCode());
            problemDetail.setProperty("timestamp", Instant.now());
            problemDetail.setProperty("path", httpRequest.getRequestURI());
            if (!params.isEmpty()) {
                problemDetail.setProperty("params", params);
            }
            return ResponseEntity.status(status).body(
                    BasicResponse.failure(problemDetail)
            );
        }

        var result = output.getData().get();
        ResponseCookie cookie = cookieUtil.createRefreshTokenCookie(result.refreshToken());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        httpResponse.addCookie(
//                cookieUtil.createRefreshTokenCookie(result.refreshToken())
//        );
        return ResponseEntity.ok(BasicResponse.success(new SignUpResponse(result.accessToken(), null, null)));

    }

    /**
     * Refresh access token using refresh token
     * POST /api/auth/refresh
     * Body: { "refreshToken": "your-refresh-token" }
     */
    @PostMapping("/refresh")
    public ResponseEntity<BasicResponse<RefreshResponse>> refreshToken(
            @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        String refreshToken = cookieUtil.getRefreshTokenFromCookies(httpRequest.getCookies());
        var output = authService.refreshAccessToken(
                refreshToken,
                httpRequest
        );

        if (output.isFailure()) {
            HttpStatus status = null;
            String detail = null;

            Map<String, Object> params = Collections.emptyMap();
            var errorCode = output.getErrorCode().get();
            if (errorCode.equals(ErrorCode.AUTH_INVALID_SESSION)) {
                status = HttpStatus.UNAUTHORIZED;
                detail = errorCode.name();
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                detail = errorCode.name();
            }

            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    status,
                    detail
            );
            problemDetail.setTitle(errorCode.getDefaultMessage());
            problemDetail.setProperty("errorCode", errorCode.getCode());
            problemDetail.setProperty("timestamp", Instant.now());
            problemDetail.setProperty("path", httpRequest.getRequestURI());
            if (!params.isEmpty()) {
                problemDetail.setProperty("params", params);
            }
            return ResponseEntity.status(status).body(
                    BasicResponse.failure(problemDetail)
            );
        }
        var result = output.getData().get();
        ResponseCookie cookie = cookieUtil.createRefreshTokenCookie(result.refreshToken());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(BasicResponse.success(
                new RefreshResponse(result.accessToken()))
        );

    }

    /**
     * Logout from current device
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<BasicResponse<LogoutResponse>> logout(
            @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookies(httpRequest.getCookies());
        var output = authService.logout(
                refreshToken
        );

        if (output.isFailure()) {
            HttpStatus status = null;
            String detail = null;

            Map<String, Object> params = Collections.emptyMap();
            var errorCode = output.getErrorCode().get();
            if (errorCode.equals(ErrorCode.AUTH_INVALID_SESSION)) {
                status = HttpStatus.UNAUTHORIZED;
                detail = errorCode.name();
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                detail = errorCode.name();
            }

            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    status,
                    detail
            );
            problemDetail.setTitle(errorCode.getDefaultMessage());
            problemDetail.setProperty("errorCode", errorCode.getCode());
            problemDetail.setProperty("timestamp", Instant.now());
            problemDetail.setProperty("path", httpRequest.getRequestURI());
            if (!params.isEmpty()) {
                problemDetail.setProperty("params", params);
            }
            return ResponseEntity.status(status).body(
                    BasicResponse.failure(problemDetail)
            );
        }
        ResponseCookie cookie = cookieUtil.createDeleteRefreshTokenCookie();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(BasicResponse.success(
                new LogoutResponse())
        );
    }

    /**
     * Logout from all devices
     * POST /api/auth/logout-all
     * Headers: Authorization: Bearer <access-token>
     */
//    @PostMapping("/logout-all")
//    public ResponseEntity<SignUpResponse> logoutAllDevices(
//            @RequestHeader("Authorization") String authHeader) {
//        try {
//            // Extract token from "Bearer <token>"
//            String token = extractTokenFromHeader(authHeader);
//            Long userId = jwtService.extractUserId(token);
//
//            authService.logoutAllDevices(userId);
//            return ResponseEntity.ok(
//                    new SignUpResponse( null, "Logged out from all devices successfully", null)
//            );
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(new SignUpResponse( null, "Invalid token", null));
//        }
//    }

    /**
     * Get list of active devices/sessions for current user
     * GET /api/auth/devices
     * Headers: Authorization: Bearer <access-token>
     */
//    @GetMapping("/devices")
//    public ResponseEntity<BasicResponse<List<ActiveDeviceDTO>>> getActiveDevices(
//            @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractTokenFromHeader(authHeader);
//            Long userId = jwtService.extractUserId(token);
//
//            List<ActiveDeviceDTO> devices = authService.getActiveDevices(userId);
//            return ResponseEntity.ok(new BasicResponse<>(devices));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(new BasicResponse<>(false, null, "Unauthorized", null));
//        }
//    }
//
//    /**
//     * Logout a specific device
//     * DELETE /api/auth/devices/{deviceId}
//     * Headers: Authorization: Bearer <access-token>
//     */
//    @DeleteMapping("/devices/{deviceId}")
//    public ResponseEntity<SignUpResponse> logoutDevice(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable String deviceId) {
//        try {
//            String token = extractTokenFromHeader(authHeader);
//            Long userId = jwtService.extractUserId(token);
//
//            authService.logoutDevice(userId, deviceId);
//            return ResponseEntity.ok(
//                    new SignUpResponse( null, "Device logged out successfully", null)
//            );
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(new SignUpResponse( null, "Invalid token", null));
//        }
//    }
//
//    /**
//     * Verify if access token is valid (optional endpoint for frontend)
//     * GET /api/auth/verify
//     * Headers: Authorization: Bearer <access-token>
//     */
//    @GetMapping("/verify")
//    public ResponseEntity<?> verifyToken(
//            @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractTokenFromHeader(authHeader);
//
//            if (jwtService.isTokenValid(token)) {
//                Long userId = jwtService.extractUserId(token);
//                String email = jwtService.extractEmail(token);
//
//                return ResponseEntity.ok(new TokenVerificationResponse(
//                        true,
//                        userId,
//                        email,
//                        "Token is valid"
//                ));
//            } else {
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body(new TokenVerificationResponse(false, null, null, "Token is invalid"));
//            }
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(new TokenVerificationResponse(false, null, null, "Token is invalid"));
//        }
//    }

}
