
package com.vp.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vp.entity.Product;
import com.vp.repository.ProductRepository;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final ProductRepository productRepository;

    public CustomerController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Customer Home Page
    @GetMapping("/home")
    public String customerHome(HttpSession session, Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        String role = (String) session.getAttribute("userRole");

        if (!"CUSTOMER".equalsIgnoreCase(role)) {
            return "redirect:/";
        }

        model.addAttribute("products", productRepository.findAll());

        return "index";
    }

    // Product Details Page
    @GetMapping("/product-details/{id}")
    public String productDetails(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        if (!"CUSTOMER".equalsIgnoreCase(
                (String) session.getAttribute("userRole"))) {
            return "redirect:/";
        }

        Product product = productRepository.findById(id)
                .orElse(null);

        if (product == null) {
            return "redirect:/customer/home";
        }

        model.addAttribute("product", product);

        return "product-details";
    }
}