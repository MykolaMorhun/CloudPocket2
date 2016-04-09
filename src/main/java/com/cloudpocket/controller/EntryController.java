package com.cloudpocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @RequestMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @RequestMapping("/logout")
    public String getLogoutPage() {
        return "logout";
    }

    @RequestMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @RequestMapping("/swagger")
    public String getSwaggerPage() {
        return "redirect:/lib/swagger/index.html";
    }
}
