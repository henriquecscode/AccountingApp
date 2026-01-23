package com.fivault.fivault.controller;

import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.service.exception.CustomException;
import com.fivault.fivault.service.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetail> handleDatabaseException(
            CustomException e,
            HttpServletRequest request) {

        logger.error("Database exception [{}]: {}", e.getCode(), e.getMessage(), e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                e.getErrorCode().getCode()
        );

        problemDetail.setTitle(e.getErrorCode().getCode());
        problemDetail.setProperty("errorCode", status.value());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());
        return ResponseEntity
                .status(status.value())
                .body(problemDetail);
    }

    // Helper class for structured validation errors
    private record ValidationError(String errorCode, Map<String, Object> params) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BasicResponse<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {


        Map<String, ValidationError> errors = new HashMap<>(); // Changed from List to single ValidationError
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationErrorCode = error.getDefaultMessage(); // This will be your error code

            // Extract constraint annotation attributes for params
            Map<String, Object> params = new HashMap<>();

            // Add field name to params
            params.put("field", fieldName);

            if (error instanceof FieldError fieldError) {
                Object rejectedValue = fieldError.getRejectedValue();

                // Add the rejected value if it's not null and not sensitive
                if (rejectedValue != null) {
                    params.put("rejectedValue", rejectedValue);
                }

                // Add common params based on validation type
                if (error.getCode() != null) {
                    switch (error.getCode()) {
                        case "Size":
                            params.put("min", error.getArguments()[2]);
                            params.put("max", error.getArguments()[1]);
                            break;
                        case "Min":
                            params.put("value", error.getArguments()[1]);
                            break;
                        case "Max":
                            params.put("value", error.getArguments()[1]);
                            break;
                        case "Pattern":
                            // You can add the pattern if needed
                            // params.put("pattern", error.getArguments()[1]);
                            break;
                    }
                }
            }

            // Only store the first validation error per field
            if (!errors.containsKey(fieldName)) {
                errors.put(fieldName, new ValidationError(validationErrorCode, params));
            }
        });

        ErrorCode errorCode = ErrorCode.VALIDATION_INVALID_INPUT;
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = errorCode.name();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                detail
        );
        problemDetail.setTitle(errorCode.getDefaultMessage());
        problemDetail.setProperty("errorCode", errorCode.getCode());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());
        if (!errors.isEmpty()) {
            problemDetail.setProperty("params", errors);
        }
        return ResponseEntity.status(status)
                .body(BasicResponse.failure(problemDetail));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception e,
            HttpServletRequest request) {

        logger.error("Unexpected exception: ", e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                "An unexpected error occurred"
        );

        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setProperty("errorCode", status.value());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        return ResponseEntity
                .status(status.value())
                .body(problemDetail);
    }
}
