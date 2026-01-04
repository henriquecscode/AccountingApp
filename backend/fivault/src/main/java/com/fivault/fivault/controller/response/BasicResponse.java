package com.fivault.fivault.controller.response;
import org.springframework.http.ProblemDetail;

public sealed interface BasicResponse<T> permits BasicResponse.Success, BasicResponse.Failure {
    record Success<T> (T data) implements BasicResponse<T>{}

    record Failure<T>(ProblemDetail problemDetail) implements  BasicResponse<T> {}

    // Factory methods
    static <T> BasicResponse<T> success(T data) {
        return new BasicResponse.Success<>(data);
    }

    static <T> BasicResponse<T> failure(ProblemDetail problemDetail) {
        return new BasicResponse.Failure<>(problemDetail);
    }
}
