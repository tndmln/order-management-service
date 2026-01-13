package com.order.management.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.order.management.auth.dto.AuthResponse;
import com.order.management.auth.dto.LoginRequest;
import com.order.management.auth.dto.RegisterRequest;
import com.order.management.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoint untuk manajemen session, registrasi, dan token JWT")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Registrasi user baru", description = "Mendaftarkan user ke sistem dan langsung mengembalikan token akses.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User berhasil didaftarkan", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "400", description = "Data tidak valid atau email sudah terdaftar", content = @Content) })
	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Login user", description = "Verifikasi kredensial dan dapatkan JWT token untuk akses API lainnya.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Login berhasil"),
			@ApiResponse(responseCode = "401", description = "Email atau password salah", content = @Content) })
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Refresh Token", description = "Gunakan Refresh Token untuk mendapatkan Access Token baru tanpa login ulang.")
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@Parameter(description = "Refresh token yang didapat saat login", required = true) @RequestParam("refreshToken") String refreshToken) {
		AuthResponse response = authService.refreshToken(refreshToken);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Logout User", description = "Menghapus session/token user agar tidak bisa digunakan lagi.")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Berhasil logout"),
			@ApiResponse(responseCode = "404", description = "User ID tidak ditemukan", content = @Content) })
	@PostMapping("/logout/{userId}")
	public ResponseEntity<Void> logout(
			@Parameter(description = "ID unik user yang ingin logout", example = "USR-001") @PathVariable String userId) {
		authService.logout(userId);
		return ResponseEntity.noContent().build();
	}
}