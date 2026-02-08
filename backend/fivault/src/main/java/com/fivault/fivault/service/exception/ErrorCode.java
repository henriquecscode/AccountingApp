package com.fivault.fivault.service.exception;


/**
 * Error codes issued by services
 */
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

    // Domain
    DOMAIN_NO_OWNER_ROLE("DOMAIN_001", "Could not fetch role for Owner", ErrorCategory.DATABASE_CATALOG),
    DOMAIN_CREATE_NO_NAME("DOMAIN_002", "Cannot create domain with empty name", ErrorCategory.VALIDATION),
    DOMAIN_CREATE_INVALID_SLUG("DOMAIN_003", "Cannot create domain with that name. Results in invalid slug", ErrorCategory.VALIDATION),
    DOMAIN_FIND_BY_OWNER_SLUG_ERROR("DOMAIN_004", "Cannot find domain by owner and slug", ErrorCategory.DATA),
    DOMAIN_NO_ROLE_FOR_DOMAIN("DOMAIN_005", "Appuser has no role in given domain", ErrorCategory.AUTHORIZATION),
    DOMAIN_FIND_BY_DOMAIN_ID_ERROR("DOMAIN_006", "Cannot find domain by domain id", ErrorCategory.DATA),
    DOMAIN_NO_VIEW_ACCESS("DOMAIN_ACCESS_001", "Appuser has no read access", ErrorCategory.AUTHORIZATION),
    DOMAIN_NO_ADMIN_ACCESS("DOMAIN_ACCESS_002", "Appuser has no admin access", ErrorCategory.AUTHORIZATION),
    DOMAIN_NO_MEMBER_ACCESS("DOMAIN_ACCESS_003", "Appuser has no member access", ErrorCategory.AUTHORIZATION),
    DOMAIN_CREATE_INVALID_SLUG_UUID("DOMAIN_003", "Cannot create domain with a name that replicates a UUID", ErrorCategory.VALIDATION),

    // AppUser
    APPUSER_FAILURE_FETCHING_APPUSER("APP_USER_001", "Could not fetch app user from authentication credentials", ErrorCategory.DATA),
    APPUSER_FIND_BY_USERNAME_ERROR("APP_USER_002", "Could not fetch app user from username", ErrorCategory.DATA),


    // Platform
    PLATFORM_NO_OWNER_ROLE("PLATFORM_001", "Could not fetch role for Platform", ErrorCategory.DATABASE_CATALOG),
    PLATFORM_CREATE_NO_NAME("PLATFORM_002", "Cannot create platform with empty name", ErrorCategory.VALIDATION),
    PLATFORM_CREATE_INVALID_SLUG("PLATFORM_003", "Cannot create platform with that name. Results in invalid slug", ErrorCategory.VALIDATION),
    PLATFORM_CREATE_INVALID_SLUG_UUID("PLATFORM_004", "Cannot create platform with a name that replicates a UUID", ErrorCategory.VALIDATION),
    PLATFORM_NO_ADMIN_ACCESS("PLATFORM_ACCESS_001", "Appuser has no admin access", ErrorCategory.AUTHORIZATION),
    PLATFORM_NO_READ_ACCESS("PLATFORM_ACCESS_002", "Appuser has no read access", ErrorCategory.AUTHORIZATION),
    PLATFORM_FIND_BY_DOMAIN_SLUG_ERROR("PLATFORM_005", "Cannot find platform by domain and slug", ErrorCategory.DATA),
    PLATFORM_FIND_BY_PLATFORM_ID_ERROR("PLATFORM_006", "Cannot find platform by platform id", ErrorCategory.DATA),


    //ACCOUNT
    ACCOUNT_NO_OWNER_ROLE("ACCOUNT_001", "Could not fetch role for Account", ErrorCategory.DATABASE_CATALOG),
    ACCOUNT_CREATE_NO_NAME("ACCOUNT_002", "Cannot create account with empty name", ErrorCategory.VALIDATION),
    ACCOUNT_CREATE_INVALID_SLUG("ACCOUNT_003", "Cannot create account with that name. Results in invalid slug", ErrorCategory.VALIDATION),
    ACCOUNT_CREATE_INVALID_SLUG_UUID("ACCOUNT_004", "Cannot create account with a name that replicates a UUID", ErrorCategory.VALIDATION),
    ACCOUNT_NO_ADMIN_ACCESS("ACCOUNT_ACCESS_001", "Appuser has no admin access", ErrorCategory.AUTHORIZATION),
    ACCOUNT_NO_READ_ACCESS("ACCOUNT_ACCESS_002", "Appuser has no read access", ErrorCategory.AUTHORIZATION),
    ACCOUNT_FIND_BY_PLATFORM_SLUG_ERROR("ACCOUNT_005", "Cannot find account by platform and slug", ErrorCategory.DATA),
    ACCOUNT_FIND_BY_ACCOUNT_ID_ERROR("ACCOUNT_006", "Cannot find account by account id", ErrorCategory.DATA),

    //EVENT
    EVENT_CREATE_NO_TITLE("EVENT_001", "Cannot create event with empty title", ErrorCategory.VALIDATION),
    EVENT_CREATE_NO_START_TIMESTAMP("EVENT_002", "Cannot create event with empty start timestamp", ErrorCategory.VALIDATION),
    EVENT_CREATE_INVALID_END_TIMESTAMP("EVENT_002", "Cannot create event where start timestamp is after end timestamp", ErrorCategory.VALIDATION),

    //EVENT TAG
    EVENTTAG_CREATE_NO_NAME("EVENTTAG_001", "Cannot create event tag with empty name", ErrorCategory.VALIDATION),
    EVENTTAG_FIND_BY_ID_ERROR("ACCOUNT_006", "Cannot find event tag by event tag id", ErrorCategory.DATA),
    EVENTTAG_DELETE_TAG_NOT_LEAF_ERROR("ACCOUNT_006", "Cannot delete tag with non deleted subtags", ErrorCategory.DATA),



    // Generic
    INTERNAL_ERROR("INT_001", "An unexpected error occurred", ErrorCategory.SYSTEM);


    public final String code;
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
        SYSTEM,
        DATABASE_CATALOG,
        DATA
    }
}