
package com.vp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {

        Object userId = session.getAttribute("loggedInUser");
        Object role = session.getAttribute("userRole");

        if (userId == null || role == null) {
            return "redirect:/login";
        }

        if ("ADMIN".equalsIgnoreCase(role.toString())) {
            return "redirect:/admin/admindash";
        }

        if ("CUSTOMER".equalsIgnoreCase(role.toString())) {
            return "redirect:/customer/home";
        }

        return "redirect:/login";
    }
}
