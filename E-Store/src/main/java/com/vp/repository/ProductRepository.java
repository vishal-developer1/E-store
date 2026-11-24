package com.vp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vp.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}