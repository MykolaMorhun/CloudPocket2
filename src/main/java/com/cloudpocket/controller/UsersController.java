package com.cloudpocket.controller;

import com.cloudpocket.model.User;
import com.cloudpocket.model.dto.UserDto;
import com.cloudpocket.services.UserService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Admin's users controller.
 */
@Api(basePath = "/api/users", value = "Users controller", description = "Operations with users")
@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "Get users info",
                  notes = "Gets detailed information about users")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public List<User> getUsers(@RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer itemsPerPage) {
        return userService.getUsers(page, itemsPerPage);
    }

    @ApiOperation(value = "Get users info",
            notes = "Gets detailed information about all users")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/all", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ApiOperation(value = "Count users",
                  notes = "Gets total number of service users")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/count", method = RequestMethod.GET,
                    produces = TEXT_PLAIN_VALUE)
    public Long countAccounts() {
        return userService.countUsers();
    }

    @ApiOperation(value = "Get user info",
                  notes = "Gets detailed information about user")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @ApiOperation(value = "Get user info",
                  notes = "Gets detailed information about user")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/login/{login}", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public User getUserByLogin(@PathVariable String login) {
        return userService.getUserByLogin(login);
    }

    @ApiOperation(value = "Add new user",
                  notes = "Add new user to the service")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(method = RequestMethod.POST,
                    consumes = APPLICATION_JSON_VALUE,
                    produces = APPLICATION_JSON_VALUE)
    public User addUser(@RequestBody UserDto user,
                        HttpServletResponse response) {
        try {
            return userService.addUser(user);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }
    }

    @ApiOperation(value = "Update user data",
                  notes = "Updates all user data except id")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 409, message = "Illegal operation with user"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/id/{id}", method = RequestMethod.PUT,
                    consumes = APPLICATION_JSON_VALUE,
                    produces = APPLICATION_JSON_VALUE)
    public User updateUser(@PathVariable Long id,
                           @RequestBody User user,
                           HttpServletResponse response) {
        if (userService.getUserById(id) != null && (user.getId() == id)) {
            try {
                return userService.updateUser(user);
            } catch (IOException e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return null;
            }
        }
        response.setStatus(HttpStatus.CONFLICT.value());
        return null;
    }

    @ApiOperation(value = "Delete user",
                  notes = "Deletes user and his data from the service")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
    }

    @ApiOperation(value = "Delete user",
                  notes = "Deletes user and his data from the service")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "login/{login}", method = RequestMethod.DELETE)
    public void deleteUserByLogin(@PathVariable String login) {
        userService.deleteUserByLogin(login);
    }

}
