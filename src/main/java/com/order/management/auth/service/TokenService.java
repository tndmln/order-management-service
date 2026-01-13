package com.order.management.auth.service;

public interface TokenService {

	void saveRefreshToken(String userId, String token);

	void revokeToken(String token);

	void revokeAllTokens(String userId);

	boolean isTokenValid(String token);
}
