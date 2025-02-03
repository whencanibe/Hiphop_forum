package org.whencanibe.crudforum.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.whencanibe.crudforum.domain.User;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(HttpSession session, Model model){
        return "home";
    }
}
