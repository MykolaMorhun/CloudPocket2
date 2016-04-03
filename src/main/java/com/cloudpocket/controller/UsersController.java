package com.cloudpocket.controller;

import com.cloudpocket.model.User;
import com.cloudpocket.model.dto.UserDto;
import com.cloudpocket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Admin's users controller.
 */
@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @RolesAllowed("admin")
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getUsers(@RequestParam(required = false) Long pageSize) {
        if (pageSize == null) pageSize = 50L;
        return userService.getUsers(); // TODO pagination
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Long countAccounts() {
        return userService.countUsers();
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/login/{login}", method = RequestMethod.GET)
    public User getUserByLogin(@PathVariable String login) {
        return userService.getUserByLogin(login);
    }

    @RolesAllowed("admin")
    @RequestMapping(method = RequestMethod.POST)
    public User addUser(@RequestBody UserDto user,
                        HttpServletResponse response) {
        try {
            return userService.addUser(user);
        } catch (IOException e) {
            response.setStatus(500);
            return null;
        }
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/id/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable Long id,
                           @RequestBody User user,
                           HttpServletResponse response) {
        if (userService.getUserById(id) != null && (user.getId() == id)) {
            try {
                return userService.updateUser(user);
            } catch (IOException e) {
                response.setStatus(500);
                return null;
            }
        }
        response.setStatus(409);
        return null;
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "login/{login}", method = RequestMethod.DELETE)
    public void deleteUserByLogin(@PathVariable String login) {
        userService.deleteUserByLogin(login);
    }

}
