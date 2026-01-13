package com.order.management.auth.service.impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.management.auth.entities.RefreshToken;
import com.order.management.auth.repositories.RefreshTokenRepository;
import com.order.management.auth.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${jwt.refresh-token.expiration-ms}")
	private long refreshTokenExpirationMs;

	/* ===================== SAVE ===================== */

	@Override
	@Transactional
	public void saveRefreshToken(String userId, String token) {

		RefreshToken refreshToken = RefreshToken.builder().userId(userId).token(token)
				.expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs)).revoked(false).build();

		refreshTokenRepository.save(refreshToken);
	}

	/* ===================== REVOKE SINGLE ===================== */

	@Override
	@Transactional
	public void revokeToken(String token) {

		refreshTokenRepository.findByToken(token).ifPresent(rt -> {
			rt.setRevoked(true);
			refreshTokenRepository.save(rt);
		});
	}

	/* ===================== REVOKE ALL ===================== */

	@Override
	@Transactional
	public void revokeAllTokens(String userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}

	/* ===================== VALIDATE ===================== */

	@Override
	public boolean isTokenValid(String token) {

		return refreshTokenRepository.findByToken(token).filter(rt -> !rt.isRevoked())
				.filter(rt -> rt.getExpiryDate().isAfter(Instant.now())).isPresent();
	}
}
