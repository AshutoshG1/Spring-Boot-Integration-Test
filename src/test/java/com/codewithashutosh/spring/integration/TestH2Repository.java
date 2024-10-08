package com.codewithashutosh.spring.integration;


import com.codewithashutosh.spring.integration.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestH2Repository extends JpaRepository<Product,Integer> {
}
