package com.sparta.myselectshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.myselectshop.entity.ProductFolder;

public interface ProductFolderRepository extends JpaRepository<ProductFolder, Long> {
}
