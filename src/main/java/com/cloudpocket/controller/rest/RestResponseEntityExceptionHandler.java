package com.cloudpocket.controller.rest;

import com.cloudpocket.exceptions.ForbiddenException;
import com.cloudpocket.model.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Exception handling controller for REST API
 */
@ControllerAdvice(annotations = RestController.class)
@RestController
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<Object> handleBadReauest(IllegalArgumentException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    protected ResponseEntity<Object> handleForbidden(ForbiddenException e) {
        return new ResponseEntity<>(new ErrorResponseDto(FORBIDDEN), FORBIDDEN);
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(FileNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDto(FORBIDDEN), NOT_FOUND);
    }

    @ExceptionHandler(value = FileAlreadyExistsException.class)
    protected ResponseEntity<Object> handleConflict(FileAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorResponseDto(CONFLICT), CONFLICT);
    }

    @ExceptionHandler(value = IOException.class)
    protected ResponseEntity<Object> handleInternalServerError(IOException e) {
        return new ResponseEntity<>(new ErrorResponseDto(INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

}