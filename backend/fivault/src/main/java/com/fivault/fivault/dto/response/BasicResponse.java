package com.fivault.fivault.dto.response;

public record BasicResponse<T>(Boolean success, String errorCode, String errorMessage, T data) {
    public BasicResponse {
    }

    public BasicResponse(T data) {
        this(true, null, null, data);
    }
}
