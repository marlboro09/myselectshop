package com.sparta.myselectshop.service;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.myselectshop.dto.KakaoUserInfoDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "KAKAO login")
@Service
@RequiredArgsConstructor
public class KakaoService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RestTemplate restTemplate;
	private final JwtUtil jwtUtil;

	public String kakaoLogin(String code) throws JsonProcessingException {
		//1. "인가 코드"로 "엑세스 토큰" 요청
		String accessToken = getToken(code);

		// 토큰이 null인지 확인
		if (accessToken == null || accessToken.isEmpty()) {
			log.error("Failed to get access token from Kakao");
			throw new IllegalArgumentException("Access token is null or empty");
		}

		//2. 토큰으로 카카오 API 호출 : "엑세스 토큰"으로 "카카오 사용자 정보" 가져오기
		KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

		//3. 필요시에 회원가입
		User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

		//4. JWT 토큰 반환
		return jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());
	}

	private String getToken(String code) throws JsonProcessingException {
		URI uri = UriComponentsBuilder
			.fromUriString("https://kauth.kakao.com")
			.path("/oauth/token")
			.encode()
			.build()
			.toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", "cd64bef7b9a28baab7c09d51663003ff");
		body.add("redirect_uri", "http://localhost:8080/api/user/kakao/callback");
		body.add("code", code);

		RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
			.post(uri)
			.headers(headers)
			.body(body);

		ResponseEntity<String> response = restTemplate.exchange(
			requestEntity,
			String.class
		);

		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		return jsonNode.get("access_token").asText();
	}

	private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
		URI uri = UriComponentsBuilder
			.fromUriString("https://kapi.kakao.com")
			.path("/v2/user/me")
			.encode()
			.build()
			.toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
			.post(uri)
			.headers(headers)
			.body(new LinkedMultiValueMap<>());

		ResponseEntity<String> response = restTemplate.exchange(
			requestEntity,
			String.class
		);

		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		Long id = jsonNode.get("id").asLong();
		String nickname = jsonNode.get("properties").get("nickname").asText();
		String email = jsonNode.get("kakao_account").get("email").asText();

		log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
		return new KakaoUserInfoDto(id, nickname, email);
	}

	private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
		Long kakaoId = kakaoUserInfo.getId();
		User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

		if (kakaoUser == null) {
			String kakaoEmail = kakaoUserInfo.getEmail();
			User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
			if (sameEmailUser != null) {
				kakaoUser = sameEmailUser;
				kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
			} else {
				String password = UUID.randomUUID().toString();
				String encodedPassword = passwordEncoder.encode(password);

				String email = kakaoUserInfo.getEmail();
				kakaoUser = new User(kakaoUserInfo.getNickname(), encodedPassword, email, UserRoleEnum.USER, kakaoId);
			}
			userRepository.save(kakaoUser);
		}
		return kakaoUser;
	}
}
