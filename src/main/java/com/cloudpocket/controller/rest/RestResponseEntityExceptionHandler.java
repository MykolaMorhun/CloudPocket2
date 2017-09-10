package com.cloudpocket.controller.rest;

import com.cloudpocket.exceptions.BadRequestException;
import com.cloudpocket.exceptions.ForbiddenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

/**
 * Exception handling controller for REST API
 */
@RestControllerAdvice(annotations = RestController.class)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    protected String handleBadReauest(IllegalArgumentException e) {
        return errorMessageToJson(e.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = ForbiddenException.class)
    protected String handleForbidden(ForbiddenException e) {
        return errorMessageToJson(e.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(value = FileNotFoundException.class)
    protected String handleNotFound(FileNotFoundException e) {
        return errorMessageToJson(e.getMessage());
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(value = FileAlreadyExistsException.class)
    protected String handleConflict(FileAlreadyExistsException e) {
        return errorMessageToJson(e.getMessage());
    }

    @ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(value = UnsupportedOperationException.class)
    protected String handleUnsupportedOperationException(UnsupportedOperationException e) {
        return errorMessageToJson(e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = IOException.class)
    protected String handleInternalServerError(IOException e) {
        return errorMessageToJson(e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(value = com.cloudpocket.exceptions.BadRequestException.class)
    protected String handleInternalServerError(BadRequestException e) {
        return errorMessageToJson(e.getMessage());
    }

    private String errorMessageToJson(String errMessage) {
        return "{\"error\":\"" + errMessage + "\"}";
    }

}