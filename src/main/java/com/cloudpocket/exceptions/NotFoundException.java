package com.cloudpocket.exceptions;

/**
 * Is used for respond with 404 error code.
 */
public class NotFoundException  extends RuntimeException {
    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }
}
