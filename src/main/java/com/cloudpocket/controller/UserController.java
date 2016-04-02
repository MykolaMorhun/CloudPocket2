package com.cloudpocket.controller;

import com.cloudpocket.model.User;
import com.cloudpocket.model.UserDto;
import com.cloudpocket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client's user controller.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    private final SimpleDateFormat JOIN_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.GET)
    public UserDto getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        User fullUserInfo = userService.getUserByLogin(userDetails.getUsername());
        return new UserDto().withLogin(fullUserInfo.getLogin())
                                    .withPassword(fullUserInfo.getPasswordHash())
                                    .withEmail(fullUserInfo.getEmail());
    }

    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.PUT)
    public UserDto updateUser(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody UserDto userInfo,
                              HttpServletResponse response) {
        if (userDetails.getUsername().equals(userInfo.getLogin())) {
            return userService.updateUser(userInfo);
        }
        response.setStatus(403);
        return null;
    }

    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.DELETE)
    public UserDto deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody UserDto userInfo,
                              HttpServletResponse response) {
        if (userDetails.getUsername().equals(userInfo.getLogin())) {
            userService.deleteUserByLogin(userInfo.getLogin());
            return userInfo;
        }
        response.setStatus(403);
        return null;
    }

    @RolesAllowed("user")
    @RequestMapping(value = "/joindate", method = RequestMethod.GET)
    public String getJoinDate(@AuthenticationPrincipal UserDetails userDetails) {
        Date joinDate = new Date(userService.getUserByLogin(userDetails.getUsername()).getJoinDate().getTime());
        return JOIN_DATE_FORMAT.format(joinDate);
    }

}
