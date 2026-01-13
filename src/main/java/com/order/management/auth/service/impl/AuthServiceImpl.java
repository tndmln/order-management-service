package com.order.management.auth.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.management.auth.dto.AuthResponse;
import com.order.management.auth.dto.LoginRequest;
import com.order.management.auth.dto.RegisterRequest;
import com.order.management.auth.entities.User;
import com.order.management.auth.repositories.UserRepository;
import com.order.management.auth.service.AuthService;
import com.order.management.auth.service.JwtService;
import com.order.management.auth.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TokenService tokenService;

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already registered");
		}

		User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.fullName(request.getFullName()).role(request.getRoles()).active(true).build();

		userRepository.save(user);

		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);

		tokenService.saveRefreshToken(user.getId(), refreshToken);

		return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
	}

	@Override
	public AuthResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		tokenService.revokeAllTokens(user.getId());

		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);

		tokenService.saveRefreshToken(user.getId(), refreshToken);

		return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
	}

	@Override
	@Transactional
	public AuthResponse refreshToken(String refreshToken) {

		if (!jwtService.validateToken(refreshToken)) {
			throw new BadCredentialsException("Invalid refresh token");
		}

		if (!tokenService.isTokenValid(refreshToken)) {
			throw new BadCredentialsException("Refresh token revoked or expired");
		}

		String userId = jwtService.extractUserId(refreshToken);

		User user = userRepository.findById(userId).orElseThrow(() -> new BadCredentialsException("User not found"));

		tokenService.revokeToken(refreshToken);

		String newAccessToken = jwtService.generateAccessToken(user);
		String newRefreshToken = jwtService.generateRefreshToken(user);

		tokenService.saveRefreshToken(userId, newRefreshToken);

		return AuthResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();
	}

	@Override
	public void logout(String userId) {
		tokenService.revokeAllTokens(userId);
	}
}
