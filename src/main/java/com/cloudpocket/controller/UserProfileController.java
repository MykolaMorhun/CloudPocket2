package com.cloudpocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for user account settings.
 */
@Controller
@RequestMapping("/profile")
public class UserProfileController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String userProfilePage() {
        return "account/userprofile";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String userSettingsPage() {
        return "account/settings";
    }

}
