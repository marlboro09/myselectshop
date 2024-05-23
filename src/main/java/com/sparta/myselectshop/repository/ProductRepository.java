package com.sparta.myselectshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findAllByUser(User user, Pageable pageable);

	Page<Product> findAllByUserAndProductFolderList_FolderId(User user, Long folderId, Pageable pageable);
	// select * from product p where p.user_id = 1;
	// select * from product p left join product_folder pf on p.id = pf.product_id where p.user_id = 1;

}
