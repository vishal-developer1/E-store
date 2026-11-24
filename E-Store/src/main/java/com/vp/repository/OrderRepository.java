
package com.vp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vp.entity.Order;

public interface OrderRepository
        extends JpaRepository<Order, Long> {
}
