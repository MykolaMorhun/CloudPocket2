package com.cloudpocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for public pages
 */
@Controller
public class EntryController {
    @RequestMapping("/")
    public String redirectToMainPage() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String getMainPage() {
        return "home";
    }

    @RequestMapping("/info")
    public String getInfoPage() {
        return "info";
    }

    @RequestMapping("login")
    public String getLoginPage() {
        return "login";
    }

    @RequestMapping("register")
    public String getRegisterPage() {
        return "register";
    }

}
