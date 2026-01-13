package com.order.management.auth.service;

import com.order.management.auth.dto.AuthResponse;
import com.order.management.auth.dto.LoginRequest;
import com.order.management.auth.dto.RegisterRequest;

public interface AuthService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	AuthResponse refreshToken(String refreshToken);

	void logout(String userId);
}
