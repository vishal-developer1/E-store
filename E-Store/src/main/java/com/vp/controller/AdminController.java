
package com.vp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.vp.entity.Product;
import com.vp.entity.Order;
import com.vp.repository.ProductRepository;
import com.vp.repository.UserRepository;
import com.vp.repository.OrderRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	UserRepository userRepository;

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;

	// Constructor Injection
	public AdminController(ProductRepository productRepository, OrderRepository orderRepository) {
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
	}

	// Admin Dashboard
	@GetMapping("/admindash")
	public String adminPage() {
		return "admin-dashboard";
	}

	// Add Product Page
	@GetMapping("/add-product")
	public String addProduct() {
		return "admin/add-product";
	}

	// Save Product with Image
	@PostMapping("/save-product")
	public String saveProduct(@RequestParam String name, @RequestParam double price, @RequestParam MultipartFile image)
			throws IOException {

		if (image.isEmpty()) {
			return "redirect:/admin/add-product";
		}

		String contentType = image.getContentType();

		if (contentType == null || !Set.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
			return "redirect:/admin/add-product";
		}

		String uploadDir = "uploads/images/";

		Files.createDirectories(Paths.get(uploadDir));

		String extension = switch (contentType) {
		case "image/jpeg" -> ".jpg";
		case "image/png" -> ".png";
		case "image/webp" -> ".webp";
		default -> throw new IllegalArgumentException("Invalid image");
		};

		String fileName = UUID.randomUUID() + extension;

		Path filePath = Paths.get(uploadDir).resolve(fileName);

		Files.copy(image.getInputStream(), filePath);

		Product product = new Product();

		product.setName(name);
		product.setPrice(price);
		product.setImage(fileName);

		productRepository.save(product);

		return "redirect:/admin/product-list";
	}

	// Product List
	@GetMapping("/product-list")
	public String productList(Model model) {

		model.addAttribute("products", productRepository.findAll());

		return "admin/product-list";
	}

	// Edit Product Page
	@GetMapping("/edit-product/{id}")
	public String editProduct(@PathVariable Long id, Model model) {

		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

		model.addAttribute("product", product);

		return "admin/edit-product";
	}

	// Update Product
	@PostMapping("/update-product")
	public String updateProduct(@RequestParam Long id, @RequestParam String name, @RequestParam double price) {

		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

		product.setName(name);
		product.setPrice(price);

		productRepository.save(product);

		return "redirect:/admin/product-list";
	}

	// Delete Product
	@PostMapping("/delete-product/{id}")
	public String deleteProduct(@PathVariable Long id) {

		productRepository.deleteById(id);

		return "redirect:/admin/product-list";
	}

	// Order List
	@GetMapping("/order-list")
	public String orderList(Model model) {

		model.addAttribute("orders", orderRepository.findAll());

		return "admin/order-list";
	}

	// Update Order Status
	@PostMapping("/update-order-status/{id}")
	public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {

		Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

		order.setStatus(status);

		orderRepository.save(order);

		return "redirect:/admin/order-list";
	}

	@GetMapping("/user-list")
	public String userList(Model model) {

		model.addAttribute("users", userRepository.findAll());

		return "admin/user-list";
	}

	@PostMapping("/delete-user/{id}")
	public String deleteUser(@PathVariable Long id) {

		userRepository.deleteById(id);

		return "redirect:/admin/user-list";
	}
}
