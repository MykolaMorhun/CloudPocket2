package com.cloudpocket.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for file manager.
 */
@Controller
@RequestMapping("/storage")
public class StorageController {

    @ModelAttribute("login")
    public String getUserLogin(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getUsername();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String openFileManager() {
        return "storage";
    }

}
