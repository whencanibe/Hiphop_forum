package org.whencanibe.crudforum.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.service.UserService;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(String email, String password, String username) {
        userService.registerUser(email, password, username);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "user/login";
    }


}
