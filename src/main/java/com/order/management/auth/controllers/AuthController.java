package com.order.management.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.order.management.auth.dto.AuthResponse;
import com.order.management.auth.dto.LoginRequest;
import com.order.management.auth.dto.RegisterRequest;
import com.order.management.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth related APIs")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Register new user")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "User registered successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request") })
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

		AuthResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Login user")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Login success"),
			@ApiResponse(responseCode = "401", description = "Invalid credentials") })
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Refresh access token")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Token refreshed"),
			@ApiResponse(responseCode = "401", description = "Invalid refresh token") })
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {

		AuthResponse response = authService.refreshToken(refreshToken);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Logout user (revoke tokens)")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Logout success") })
	@PostMapping("/logout/{userId}")
	public ResponseEntity<Void> logout(@PathVariable String userId) {

		authService.logout(userId);
		return ResponseEntity.noContent().build();
	}
}
