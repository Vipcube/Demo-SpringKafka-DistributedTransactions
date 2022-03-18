package org.vipcube.spring.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vipcube.spring.inventory.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
