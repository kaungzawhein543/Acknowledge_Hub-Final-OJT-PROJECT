package com.ace.utility.core.coreResponse;

public enum MessageConstants {

    SUCCESS(200),
    BAD_REQUEST_ERROR(400),
    INTERNAL_SERVER_ERROR(500),
    UNAUTHORIZED_ERROR(401),
    NOT_FOUND_ERROR(404);

    private final int statusCode;
    MessageConstants(int code) {
        this.statusCode = code;
    }

    public int getCode() {
        return statusCode;
    }
}
