package com.sparta.myselectshop.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

	private final ProductService productService;

	@PostMapping("/products")
	public ProductRequestDto createProduct(@RequestBody ProductRequestDto requestDto) {
		return productService.createProduct(requestDto);
	}
}
