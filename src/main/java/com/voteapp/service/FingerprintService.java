package com.voteapp.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * FingerprintService
 *
 * Real production-la: actual biometric SDK (e.g. Suprema, DigitalPersona) use
 * pannuvan — encrypted minutiae template DB-la store aagum.
 *
 * Inga: SHA-256 hash simulate pannurom — • Register time: fingerprintToken →
 * hash → DB store • Vote time : fingerprintToken → hash → DB hash compare →
 * match check
 */

@Service
@Slf4j
public class FingerprintService {

	/**
	 * Fingerprint token-ai hash pannurom (SHA-256). Real system-la: biometric
	 * template encrypt panni store pannuvan.
	 */
	public String hashFingerprint(String fingerprintToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(fingerprintToken.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 not available", e);
		}
	}

	/**
	 * Vote time-la: incoming token hash == stored hash ah check pannurom.
	 */
	public boolean verifyFingerprint(String incomingToken, String storeHash) {
		if (storeHash == null || storeHash.isBlank()) {
			log.info("No fingerprint hash found in DB — voter not biometrically enrolled!");
			return false;
		}
		String incomingHash = hashFingerprint(incomingToken);
		boolean match = incomingHash.equals(storeHash);
		if (!match) {
			log.warn("Fingerprint mismatch! incoming={} stored={}", incomingHash, storeHash);
		}
		return match;
	}
}
