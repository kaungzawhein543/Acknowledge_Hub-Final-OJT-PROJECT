package com.ace.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String fileName;

    public ErrorResponse(int status, String message, String fileName) {
        this.status = status;
        this.message = message;
        this.fileName = fileName;
    }
}
