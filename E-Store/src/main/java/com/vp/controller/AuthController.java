
package com.vp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.vp.entity.User;
import com.vp.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            model.addAttribute("error", "All fields are required");
            return "register";
        }

        if (password.length() < 8) {
            model.addAttribute("error", "Password must contain at least 8 characters");
            return "register";
        }

        String userEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(userEmail)) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(userEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("CUSTOMER");

        userRepository.save(user);

        return "redirect:/login?registered=true";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        String userEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(userEmail).orElse(null);

        if (user == null ||
                !passwordEncoder.matches(password, user.getPassword())) {

            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        session.setAttribute("loggedInUser", user.getId());
        session.setAttribute("userRole", user.getRole());

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin/admindash";
        }

        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/customer/home";
        }

        session.invalidate();
        model.addAttribute("error", "Invalid user role");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
