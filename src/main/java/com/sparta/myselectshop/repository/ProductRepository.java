package com.sparta.myselectshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByUser(User user);
}
