package com.cloudpocket.exceptions;

/**
 * Is used for respond with 409 error code.
 */
public class ConflictException extends RuntimeException {

    public ConflictException() {
        super();
    }

    public ConflictException(String message) {
        super(message);
    }
}
