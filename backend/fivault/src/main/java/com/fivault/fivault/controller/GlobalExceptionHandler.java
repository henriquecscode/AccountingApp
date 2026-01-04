package com.fivault.fivault.controller;

import com.fivault.fivault.service.exception.CustomException;
import com.fivault.fivault.service.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

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
