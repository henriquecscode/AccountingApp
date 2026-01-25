package com.fivault.fivault.util;

import com.fivault.fivault.controller.response.BasicResponse;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.Output;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public class ControllerOutputFailureUtil {


    public static <T> ResponseEntity<BasicResponse<T>> GetResponseError(HttpServletRequest httpRequest, Output output) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> params = Collections.emptyMap();
        ErrorCode errorCode = (ErrorCode) output.getErrorCode().orElse(
                ErrorCode.INTERNAL_ERROR
        );

        String detail = errorCode.name();

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
}
