package com.capstone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller  // Changed from @RestController
@RequestMapping("/")  // Base path
public class HomeController {

    @GetMapping  // Maps to "/"
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Capstone Project Management System!");
        return "home";  // Returns home.html from Thymeleaf templates
    }

    @GetMapping("/dashboard")  // Maps to "/dashboard"
    public String dashboard() {
        return "dashboard";  // Returns dashboard.html from Thymeleaf templates
    }
}
