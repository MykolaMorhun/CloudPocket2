package com.cloudpocket.controller;

import com.cloudpocket.exceptions.UserAlreadyExistException;
import com.cloudpocket.model.dto.UserDto;
import com.cloudpocket.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Controller for registration process.
 */
@Controller
public class RegistrationController {

    @Autowired
    RegistrationService registrationService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute UserDto newUser) throws IOException {
        registrationService.registerNewUser(newUser);
        return "redirect:/storage";
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException() {
        return returnError("Internal server error");
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public String handleUserAlreadyExistException(HttpServletRequest request) {
        String login = request.getParameter("login");
        return returnError("User with login \"" + login + "\" already exists");
    }

    private String returnError(String errorMessage) {
        return "redirect:/register?error=" + errorMessage;
    }

}
