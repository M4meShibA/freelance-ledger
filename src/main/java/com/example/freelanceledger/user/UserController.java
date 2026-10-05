package com.example.freelanceledger.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String email,
                            Model model) {
        try {
            userService.register(username, password, email);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/register";
        }
    }
}
