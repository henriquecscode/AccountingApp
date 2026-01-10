package com.fivault.fivault.service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Database errors
    DB_DUPLICATE_KEY("DB_001", "Resource already exists", ErrorCategory.DATABASE),
    DB_INTEGRITY_VIOLATION("DB_002", "Data integrity constraint violated", ErrorCategory.DATABASE),
    DB_CONNECTION_ERROR("DB_003", "Database connection failed", ErrorCategory.DATABASE),
    DB_TIMEOUT("DB_004", "Database operation timed out", ErrorCategory.DATABASE),
    DB_OPERATION_FAILED("DB_999", "Database operation failed", ErrorCategory.DATABASE),

    // Authentication errors
    AUTH_USER_EXISTS("AUTH_001", "User already exists", ErrorCategory.AUTHENTICATION),
    AUTH_INVALID_CREDENTIALS("AUTH_002", "Invalid credentials", ErrorCategory.AUTHENTICATION),
    AUTH_INVALID_SESSION("AUTH_004", "Invalid session", ErrorCategory.AUTHENTICATION),
    AUTH_WEAK_PASSWORD("AUTH_003", "Password does not meet requirements", ErrorCategory.AUTHENTICATION),

    // Validation errors
    VALIDATION_INVALID_INPUT("VAL_001", "Invalid input provided", ErrorCategory.VALIDATION),
    VALIDATION_MISSING_FIELD("VAL_002", "Required field is missing", ErrorCategory.VALIDATION),

    // Generic
    INTERNAL_ERROR("INT_001", "An unexpected error occurred", ErrorCategory.SYSTEM);

    private final String code;
    private final String defaultMessage;
    private final ErrorCategory category;

    ErrorCode(String code, String defaultMessage, ErrorCategory category) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
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