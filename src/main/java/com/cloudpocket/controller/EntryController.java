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
        return "welcome/home";
    }

    @RequestMapping("/info")
    public String getInfoPage() {
        return "welcome/info";
    }

    @RequestMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @RequestMapping("/logout")
    public String getLogoutPage() {
        return "logout";
    }

    @RequestMapping("register")
    public String getRegisterPage() {
        return "welcome/register";
    }

    @RequestMapping("/swagger")
    public String getSwaggerPage() {
        return "redirect:/lib/swagger/index.html";
    }
}
