package com.fivault.fivault.exception;

import org.springframework.http.HttpStatus;


import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Database errors
    DB_DUPLICATE_KEY("DB_001", "Resource already exists", HttpStatus.CONFLICT, ErrorCategory.DATABASE),
    DB_INTEGRITY_VIOLATION("DB_002", "Data integrity constraint violated", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCategory.DATABASE),
    DB_CONNECTION_ERROR("DB_003", "Database connection failed", HttpStatus.SERVICE_UNAVAILABLE, ErrorCategory.DATABASE),
    DB_TIMEOUT("DB_004", "Database operation timed out", HttpStatus.REQUEST_TIMEOUT, ErrorCategory.DATABASE),
    DB_OPERATION_FAILED("DB_999", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCategory.DATABASE),

    // Authentication errors
    AUTH_USER_EXISTS("AUTH_001", "User already exists", HttpStatus.CONFLICT, ErrorCategory.AUTHENTICATION),
    AUTH_INVALID_CREDENTIALS("AUTH_002", "Invalid credentials", HttpStatus.UNAUTHORIZED, ErrorCategory.AUTHENTICATION),
    AUTH_WEAK_PASSWORD("AUTH_003", "Password does not meet requirements", HttpStatus.BAD_REQUEST, ErrorCategory.AUTHENTICATION),

    // Validation errors
    VALIDATION_INVALID_INPUT("VAL_001", "Invalid input provided", HttpStatus.BAD_REQUEST, ErrorCategory.VALIDATION),
    VALIDATION_MISSING_FIELD("VAL_002", "Required field is missing", HttpStatus.BAD_REQUEST, ErrorCategory.VALIDATION),

    // Generic
    INTERNAL_ERROR("INT_001", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCategory.SYSTEM);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
    private final ErrorCategory category;

    ErrorCode(String code, String defaultMessage, HttpStatus httpStatus, ErrorCategory category) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorCategory getCategory() {
        return category;
    }

    public enum ErrorCategory {
        DATABASE,
        AUTHENTICATION,
        AUTHORIZATION,
        VALIDATION,
        BUSINESS_LOGIC,
        SYSTEM
    }
}