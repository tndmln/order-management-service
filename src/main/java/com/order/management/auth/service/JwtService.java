package com.order.management.auth.service;

import java.util.List;

import com.order.management.auth.entities.User;

public interface JwtService {

	String generateAccessToken(User user);

	String generateRefreshToken(User user);

	Boolean validateToken(String token);

	String extractUserId(String token);

	List<String> extractRoles(String token);
}
