package com.cloudpocket.model.dto;

import org.springframework.http.HttpStatus;

/**
 * Object for transfer data about an error.
 */
public class ErrorResponseDto {
    private String message;
    private int code;

    public ErrorResponseDto(HttpStatus status) {
        code = status.value();
        message = status.getReasonPhrase();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}