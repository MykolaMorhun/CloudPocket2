package com.cloudpocket.exceptions;

/**
 * Is used for respond with 400 error code.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super();
    }

    public BadRequestException(String message) {
        super(message);
    }

}
