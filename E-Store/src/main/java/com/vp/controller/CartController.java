
package com.vp.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.vp.entity.Product;
import com.vp.repository.ProductRepository;

@Controller
public class CartController {

    private final ProductRepository productRepository;

    public CartController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Map<Long, Integer> cart =
                (Map<Long, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        return cart;
    }

    // Add product to cart
    @PostMapping("/cart/add/{id}")
    public String addToCart(
            @PathVariable Long id,
            HttpSession session) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Map<Long, Integer> cart = getCart(session);

        cart.merge(product.getId(), 1, Integer::sum);

        return "redirect:/cart";
    }

    // Display cart
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {

        Map<Long, Integer> cart = getCart(session);

        List<Map<String, Object>> cartItems = new ArrayList<>();
        double total = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Product product = productRepository.findById(entry.getKey())
                    .orElse(null);

            if (product != null) {
                int quantity = entry.getValue();
                double subtotal = product.getPrice() * quantity;

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("product", product);
                item.put("quantity", quantity);
                item.put("subtotal", subtotal);

                cartItems.add(item);
                total += subtotal;
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }

    // Remove product from cart
    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(
            @PathVariable Long id,
            HttpSession session) {

        Map<Long, Integer> cart = getCart(session);
        cart.remove(id);

        return "redirect:/cart";
    }
}
