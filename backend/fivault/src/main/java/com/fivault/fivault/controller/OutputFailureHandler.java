package com.fivault.fivault.controller;

import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.Output;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class OutputFailureHandler {

    // Default mapping used by all endpoints unless overridden
    private static final Map<ErrorCode, HttpStatus> DEFAULT_MAPPING =
            new EnumMap<>(ErrorCode.class);

    static {
        // Database errors
        DEFAULT_MAPPING.put(ErrorCode.DB_DUPLICATE_KEY, HttpStatus.CONFLICT);
        DEFAULT_MAPPING.put(ErrorCode.DB_INTEGRITY_VIOLATION, HttpStatus.BAD_REQUEST);
        DEFAULT_MAPPING.put(ErrorCode.DB_CONNECTION_ERROR, HttpStatus.SERVICE_UNAVAILABLE);
        DEFAULT_MAPPING.put(ErrorCode.DB_TIMEOUT, HttpStatus.REQUEST_TIMEOUT);
        DEFAULT_MAPPING.put(ErrorCode.DB_OPERATION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);

        // Authentication errors
        DEFAULT_MAPPING.put(ErrorCode.AUTH_USER_EXISTS, HttpStatus.CONFLICT);
        DEFAULT_MAPPING.put(ErrorCode.AUTH_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        DEFAULT_MAPPING.put(ErrorCode.AUTH_INVALID_SESSION, HttpStatus.UNAUTHORIZED);
        DEFAULT_MAPPING.put(ErrorCode.AUTH_WEAK_PASSWORD, HttpStatus.BAD_REQUEST);

        // Validation errors
        DEFAULT_MAPPING.put(ErrorCode.VALIDATION_INVALID_INPUT, HttpStatus.BAD_REQUEST);
        DEFAULT_MAPPING.put(ErrorCode.VALIDATION_MISSING_FIELD, HttpStatus.BAD_REQUEST);

        // Domain errors
        DEFAULT_MAPPING.put(ErrorCode.DOMAIN_NO_OWNER_ROLE, HttpStatus.INTERNAL_SERVER_ERROR);
        DEFAULT_MAPPING.put(ErrorCode.DOMAIN_CREATE_NO_NAME, HttpStatus.BAD_REQUEST);
        DEFAULT_MAPPING.put(ErrorCode.DOMAIN_CREATE_INVALID_SLUG, HttpStatus.BAD_REQUEST);

        // AppUser errors
        DEFAULT_MAPPING.put(ErrorCode.APPUSER_FAILURE_FETCHING_APPUSER, HttpStatus.INTERNAL_SERVER_ERROR);
        DEFAULT_MAPPING.put(ErrorCode.APPUSER_FIND_BY_USERNAME_ERROR, HttpStatus.NOT_FOUND);

        // Generic
        DEFAULT_MAPPING.put(ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Configuration for a specific error code
    public static class ErrorConfiguration {
        private final HttpStatus status;
        private final Map<String, Object> params;

        public ErrorConfiguration(HttpStatus status, Map<String, Object> params) {
            this.status = status;
            this.params = params != null ? params : Collections.emptyMap();
        }

        public ErrorConfiguration(HttpStatus status) {
            this(status, Collections.emptyMap());
        }

        public HttpStatus getStatus() {
            return status;
        }

        public Map<String, Object> getParams() {
            return params;
        }
    }

    public static <R> ResponseEntity<BasicResponse<R>> handleOutputFailure(
            HttpServletRequest httpRequest,
            Output<?> output) {
        return handleOutputFailure(httpRequest, output, Collections.emptyMap());
    }

    // Full customization - status and parameters per error code
    public static <R> ResponseEntity<BasicResponse<R>> handleOutputFailure(
            HttpServletRequest httpRequest,
            Output<?> output,
            Map<ErrorCode, ErrorConfiguration> customConfigurations) {

        var errorCode = output.getErrorCode().get();

        // Get configuration (custom or default)
        ErrorConfiguration config = customConfigurations.get(errorCode);

        HttpStatus status;
        Map<String, Object> params;

        if (config != null) {
            // Use custom configuration
            status = config.getStatus();
            params = config.getParams();
        } else {
            // Use default mapping
            status = DEFAULT_MAPPING.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
            params = Collections.emptyMap();
        }

        String detail = errorCode.getCode();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                detail
        );
        problemDetail.setTitle(errorCode.getDefaultMessage());
        problemDetail.setProperty("errorCode", errorCode.getCode());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", httpRequest.getRequestURI());

        // Add custom parameters if provided
        if (!params.isEmpty()) {
            problemDetail.setProperty("params", params);
        }

        return ResponseEntity.status(status).body(
                BasicResponse.failure(problemDetail)
        );
    }
}