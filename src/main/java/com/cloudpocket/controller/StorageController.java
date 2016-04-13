package com.cloudpocket.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

/**
 * Controller
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @RequestMapping(method = RequestMethod.GET)
    public String listDirectory(@AuthenticationPrincipal UserDetails userDetails) {
        return "storage";
    }

}
