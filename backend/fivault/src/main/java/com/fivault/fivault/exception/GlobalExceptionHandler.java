package com.fivault.fivault.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetail> handleDatabaseException(
            CustomException e,
            HttpServletRequest request) {

        logger.error("Database exception [{}]: {}", e.getCode(), e.getMessage(), e);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getHttpStatus(),
                e.getMessage()
        );

        problemDetail.setTitle(e.getHttpStatus().getReasonPhrase());
        problemDetail.setProperty("errorCode", e.getCode());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());
        return ResponseEntity
                .status(e.getHttpStatus())  // Use status from enum!
                .body(problemDetail);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExists(
            UserAlreadyExistsException e,
            HttpServletRequest request) {

        logger.warn("User already exists: {}", e.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                e.getMessage()
        );

        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setProperty("errorCode", status.value());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        return ResponseEntity
                .status(status.value())
                .body(problemDetail);
    }

//    @ExceptionHandler(WeakPasswordException.class)
//    public ResponseEntity<ErrorResponse> handleWeakPassword(
//            WeakPasswordException e,
//            HttpServletRequest request) {
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                ErrorCode.AUTH_WEAK_PASSWORD.getCode(),
//                e.getMessage(),
//                request.getRequestURI()
//        );
//
//        return ResponseEntity
//                .status(ErrorCode.AUTH_WEAK_PASSWORD.getHttpStatus())
//                .body(errorResponse);
//    }

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
                .status(ErrorCode.INTERNAL_ERROR.getHttpStatus())
                .body(problemDetail);
    }
}
