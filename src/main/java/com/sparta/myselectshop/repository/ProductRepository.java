package com.sparta.myselectshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.myselectshop.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
