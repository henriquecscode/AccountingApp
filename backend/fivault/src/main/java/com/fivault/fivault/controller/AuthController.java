package com.fivault.fivault.controller;

import com.fivault.fivault.controller.request.RefreshRequest;
import com.fivault.fivault.controller.request.SignInRequest;
import com.fivault.fivault.controller.request.SignUpRequest;
import com.fivault.fivault.controller.response.RefreshResponse;
import com.fivault.fivault.controller.response.SignUpResponse;
import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.service.AuthService;
import com.fivault.fivault.service.JwtService;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.output.Output;
import com.fivault.fivault.service.output.SignUpResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.fivault.fivault.util.CookieUtil;

import java.time.Instant;

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
    public ResponseEntity<BasicResponse<String>> hi() {
        return ResponseEntity.ok(BasicResponse.success("Authenticated"));
    }

    @GetMapping("/try-not-authenticated")
    public String hiNotauth() {
        return "Not Authenticated";
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
                request.email(),
                request.password(),
                httpRequest
        );

        if (output.isFailure()) {
            HttpStatus status = null;
            String detail = null;


            var errorCode = output.getErrorCode().get();
            if (errorCode.equals(ErrorCode.AUTH_USER_EXISTS)) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                detail = errorCode.getCode();
            } else if (errorCode.equals(ErrorCode.VALIDATION_INVALID_INPUT)) {
                status = HttpStatus.BAD_REQUEST;
                detail = errorCode.getCode();
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                detail = errorCode.getCode();
            }

            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    status,
                    detail
            );
            problemDetail.setTitle(status.getReasonPhrase());
            problemDetail.setProperty("errorCode", detail);
            problemDetail.setProperty("timestamp", Instant.now());
            problemDetail.setProperty("path", httpRequest.getRequestURI());
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
     * POST /api/auth/signin
     * Body: { "email": "user@example.com", "password": "password123" }
     */
    @PostMapping("/signin")
    public ResponseEntity<BasicResponse<SignUpResponse>> signIn(
            @RequestBody SignInRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        var output = authService.signIn(
                request.email(),
                request.password(),
                httpRequest
        );

        if (output.isFailure()) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    status,
                    null
            );
            return ResponseEntity.status(status).body(
                    BasicResponse.failure(problem)
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
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    status,
                    null
            );
            return ResponseEntity.status(status).body(
                    BasicResponse.failure(problem)
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
     * Body: { "refreshToken": "your-refresh-token" }
     */
    @PostMapping("/logout")
    public ResponseEntity<SignUpResponse> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(
                new SignUpResponse(null, "Logged out successfully", null)
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
