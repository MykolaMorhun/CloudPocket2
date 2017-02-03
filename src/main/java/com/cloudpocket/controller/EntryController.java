package com.cloudpocket.controller;

import com.cloudpocket.services.CloudPocketOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.cloudpocket.config.Properties.ADMIN_EMAIL_PROPERTY;
import static com.cloudpocket.config.Properties.IS_SELF_REGISTRATION_ALLOWED_PROPERTY;
import static com.cloudpocket.utils.Utils.firstIfNotNull;

/**
 * Controller for public pages.
 */
@Controller
public class EntryController {
    @Autowired
    private CloudPocketOptionsService optionsService;

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
    public String getRegisterPage(Model model) {
        model.addAttribute("isSelfRegistrationAllowed",
                           firstIfNotNull(optionsService.getOption(IS_SELF_REGISTRATION_ALLOWED_PROPERTY), true));
        model.addAttribute("adminEmail", optionsService.getOption(ADMIN_EMAIL_PROPERTY));

        return "welcome/register";
    }

    @RequestMapping("/swagger")
    public String getSwaggerPage() {
        return "redirect:/lib/swagger/index.html";
    }
}
