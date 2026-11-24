package com.vp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.vp.entity.Order;
import com.vp.repository.OrderRepository;

@Controller
public class OrderStatusController {
	
	 @Autowired
	    private OrderRepository orderRepository;

	    @GetMapping("/order-status/{id}")
	    public String viewOrderStatus(@PathVariable("id") Long id, Model model) {

	        Optional<Order> optionalOrder = orderRepository.findById(id);

	        if (optionalOrder.isEmpty()) {
	            return "redirect:/customer/home";
	        }

	        model.addAttribute("order", optionalOrder.get());

	        return "order-status";
	    }
	}


