package com.sparta.myselectshop.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.ProductFolder;
import com.sparta.myselectshop.entity.User;

public interface ProductFolderRepository extends JpaRepository<ProductFolder, Long> {
	Optional<ProductFolder> findByProductAndFolder(Product product, Folder folder);

}
