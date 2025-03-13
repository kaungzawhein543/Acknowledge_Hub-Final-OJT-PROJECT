package com.ace.utility.exceptions;

import lombok.Data;

@Data
public class ResourceNotFoundException extends RuntimeException{
    private int statusCode;
    private String fileName;

    public ResourceNotFoundException(String message, int statusCode, String fileName) {
        super(message);
        this.statusCode = statusCode;
        this.fileName = fileName;
    }
}
