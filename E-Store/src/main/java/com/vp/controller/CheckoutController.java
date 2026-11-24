
package com.vp.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.vp.entity.Order;
import com.vp.entity.Product;
import com.vp.repository.OrderRepository;
import com.vp.repository.ProductRepository;

@Controller
public class CheckoutController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public CheckoutController(
            ProductRepository productRepository,
            OrderRepository orderRepository) {

        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // Display checkout page
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {

        Map<Long, Integer> cart =
                (Map<Long, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        double total = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Product product = productRepository
                    .findById(entry.getKey())
                    .orElse(null);

            if (product == null) {
                return "redirect:/cart";
            }

            total += product.getPrice() * entry.getValue();
        }

        model.addAttribute("total", total);

        return "checkout";
    }

    // Place order using Cash on Delivery
    @PostMapping("/place-order")
    public String placeOrder(

            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam String address,

            HttpSession session) {

        Map<Long, Integer> cart =
                (Map<Long, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Validate customer details
        if (customerName == null || customerName.isBlank()
                || email == null || email.isBlank()
                || address == null || address.isBlank()) {

            return "redirect:/checkout";
        }

        ArrayList<Order> orders = new ArrayList<>();

        // Create order for each product
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Product product = productRepository
                    .findById(entry.getKey())
                    .orElse(null);

            if (product == null) {
                return "redirect:/cart";
            }

            Integer quantity = entry.getValue();

            if (quantity == null || quantity <= 0) {
                return "redirect:/cart";
            }

            Order order = new Order();

            order.setCustomerName(customerName.trim());
            order.setEmail(email.trim());
            order.setAddress(address.trim());

            order.setProductName(product.getName());
            order.setQuantity(quantity);

            order.setTotalPrice(
                    product.getPrice() * quantity
            );

            order.setOrderDate(LocalDateTime.now());

            order.setStatus("Pending");

            // Cash on Delivery only
            order.setPaymentMethod("CASH_ON_DELIVERY");

            orders.add(order);
        }

        if (orders.isEmpty()) {
            return "redirect:/cart";
        }

        // Save orders in database
        orderRepository.saveAll(orders);

        // Clear cart after successful order placement
        session.removeAttribute("cart");

        return "redirect:/order-success";
    }

    // Order success page
    @GetMapping("/order-success")
    public String orderSuccess() {

        return "order-success";
    }
    
}

