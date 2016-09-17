package com.cloudpocket.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundException(Model model, FileNotFoundException e) {
        model.addAttribute("pageTitle", "Not Found");
        model.addAttribute("statusCode", 404);
        model.addAttribute("errorMsg", e.getMessage());
        return "errors/error_template";
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public String handleMissingRequestParameter(Model model, Exception e) {
        model.addAttribute("pageTitle", "Bad request");
        model.addAttribute("statusCode", 400);
        model.addAttribute("errorMsg", e.getMessage());
        return "errors/error_template";
    }

    @ExceptionHandler(Exception.class)
    public String otherException(Model model, Exception e) {
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorMsg", e.getMessage());
        return "errors/error_template";
    }

}
