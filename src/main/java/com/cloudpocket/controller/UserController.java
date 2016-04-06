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
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client's user controller.
 */
@Api(basePath = "//api/user", value = "usercontrol", description = "Operations with user", produces = "application/json")
@RestController
@RequestMapping(value = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    private UserService userService;

    private final SimpleDateFormat JOIN_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    @ApiOperation(value = "Get information about user",
                  notes = "Gets information about current user")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "") })
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        User fullUserInfo = userService.getUserByLogin(userDetails.getUsername());
        return new UserDto().withLogin(fullUserInfo.getLogin())
                .withPassword(fullUserInfo.getPasswordHash())
                .withEmail(fullUserInfo.getEmail());
    }

    @ApiOperation(value = "Update user",
            notes = "Updates user profile")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "Update success") })
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto updateUser(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody(required = true) UserDto userInfo,
                              HttpServletResponse response) {
        if (userDetails.getUsername().equals(userInfo.getLogin())) {
            return userService.updateUser(userInfo);
        } else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }

    @ApiOperation(value = "Delete user",
                  notes = "Deletes user's account. User details is used as confirmation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "Deletion success") })
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.DELETE,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody(required = true) UserDto userInfo,
                              HttpServletResponse response) {
        if (userDetails.getUsername().equals(userInfo.getLogin())) {
            userService.deleteUserByLogin(userInfo.getLogin());
            return userInfo;
        }
        response.setStatus(403);
        return null;
    }

    @ApiOperation(value = "Get user registration date",
            notes = "Gets date of user registration in format: 'dd.MM.yyyy hh:mm:ss'")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 200, message = "") })
    @RolesAllowed("user")
    @RequestMapping(value = "/joindate", method = RequestMethod.GET,
                    produces = MediaType.TEXT_PLAIN_VALUE)
    public String getJoinDate(@AuthenticationPrincipal UserDetails userDetails) {
        Date joinDate = new Date(userService.getUserByLogin(userDetails.getUsername()).getJoinDate().getTime());
        return JOIN_DATE_FORMAT.format(joinDate);
    }

}
