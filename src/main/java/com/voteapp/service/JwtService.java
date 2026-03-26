package com.voteapp.service;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

	@Value("${app.jwt.secret}")
	private String jwtSecret;

	// OTP session token valid for 15 minutes
	private static final long OTP_SESSION_EXPRIY_MS = 15 * 60 * 1000;

	private final Set<String> usedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes()));
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateOtpSessionToken(String voterId) {
		return Jwts.builder().subject(voterId).claim("type", "OTP_SESSION").issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + OTP_SESSION_EXPRIY_MS)).signWith(getSigningKey())
				.compact();
	}

	public boolean validateOtpSessionToken(String token, String voterId) {
		try {

			if (usedTokens.contains(token)) {
				log.warn("BLOCKED: Attempt to reuse already-consumed OTP session token for voter: {}", voterId);
				return false;
			}

			Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
			return claims.getSubject().equals(voterId) && "OTP_SESSION".equals(claims.get("type", String.class))
					&& claims.getExpiration().after(new Date());
		} catch (JwtException e) {
			log.warn("Invalid OTP session token: {}", e.getMessage());
			return false;
		}
	}

	public void consumeToken(String token) {
		usedTokens.add(token);
		log.info("OTP session token consumed and blacklisted - cannot be reused.");
	}
}
