package com.sparta.myselectshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.myselectshop.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByKakaoId(Long kakaoId);
}