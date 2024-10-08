package com.codewithashutosh.spring.integration.repository;


import com.codewithashutosh.spring.integration.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    Product findByName(String name);
}

