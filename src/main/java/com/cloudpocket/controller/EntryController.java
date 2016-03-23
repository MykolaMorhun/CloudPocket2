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
        return "redirect:/main";
    }

    @RequestMapping("/main")
    public String getMainPage() {
        return "main";
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
