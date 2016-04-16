package com.cloudpocket.controller.rest;

import com.cloudpocket.exceptions.ForbiddenException;
import com.cloudpocket.model.User;
import com.cloudpocket.model.dto.UserDto;
import com.cloudpocket.services.UserService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.http.MediaType.*;

/**
 * Client's user controller.
 */
@Api(basePath = "/api/user", value = "User controller", description = "Operations with a user")
@RestController
@RequestMapping(value = "/api/user", produces = APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    private UserService userService;

    private final SimpleDateFormat JOIN_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    @ApiOperation(value = "Get information about user",
                  notes = "Gets information about current user")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "") })
    @RequestMapping(method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public UserDto getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        User fullUserInfo = userService.getUserByLogin(userDetails.getUsername());
        return new UserDto().withLogin(fullUserInfo.getLogin())
                            .withPassword("<hidden>")
                            .withEmail(fullUserInfo.getEmail());
    }

    @ApiOperation(value = "Update user",
                  notes = "Updates user profile")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "Update success") })
    @RequestMapping(method = RequestMethod.PUT,
                    consumes = APPLICATION_JSON_VALUE,
                    produces = APPLICATION_JSON_VALUE)
    public UserDto updateUser(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody(required = true) UserDto userInfo) {
        if (userDetails.getUsername().equals(userInfo.getLogin())) {
            return userService.updateUser(userInfo);
        }
        throw new ForbiddenException();
    }

    @ApiOperation(value = "Delete user",
                  notes = "Deletes user's account. User details is used as confirmation")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "Deletion success") })
    @RequestMapping(method = RequestMethod.DELETE,
                    consumes = APPLICATION_JSON_VALUE,
                    produces = APPLICATION_JSON_VALUE)
    public UserDto deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody(required = true) UserDto userInfo) {
        if (userDetails.getUsername().equals(userInfo.getLogin())) {
            userService.deleteUserByLogin(userInfo.getLogin());
            return userInfo;
        }
        throw new ForbiddenException();
    }

    @ApiOperation(value = "Get user registration date",
                  notes = "Gets date of user registration in the format: 'dd.MM.yyyy hh:mm:ss'")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "") })
    @RequestMapping(value = "/joindate", method = RequestMethod.GET,
                    produces = TEXT_PLAIN_VALUE)
    public String getJoinDate(@AuthenticationPrincipal UserDetails userDetails) {
        Date joinDate = new Date(userService.getUserByLogin(userDetails.getUsername()).getJoinDate().getTime());
        return JOIN_DATE_FORMAT.format(joinDate);
    }

}
