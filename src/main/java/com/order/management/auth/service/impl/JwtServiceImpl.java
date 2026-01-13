package com.order.management.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.order.management.auth.entities.User;
import com.order.management.auth.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.access-token.expiration-ms}")
	private long accessTokenExpirationMs;

	@Value("${jwt.refresh-token.expiration-ms}")
	private long refreshTokenExpirationMs;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String generateAccessToken(User user) {
		Instant now = Instant.now();

		return Jwts.builder().subject(user.getId()).claim("roles", user.getRole()).issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(accessTokenExpirationMs))).signWith(getSigningKey()).compact();
	}

	@Override
	public String generateRefreshToken(User user) {
		Instant now = Instant.now();

		return Jwts.builder().subject(user.getId()).issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(refreshTokenExpirationMs))).signWith(getSigningKey()).compact();
	}

	@Override
	public Boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public String extractUserId(String token) {
		return parseClaims(token).getSubject();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {
		return parseClaims(token).get("roles", List.class);
	}

	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}
}
