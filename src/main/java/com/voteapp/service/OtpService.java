package com.voteapp.service;

import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.voteapp.dto.OtpDTOs.OtpResponse;
import com.voteapp.dto.OtpDTOs.SendOtpRequest;
import com.voteapp.dto.OtpDTOs.VerifyOtpRequest;
import com.voteapp.entity.OtpSession;
import com.voteapp.entity.Voter;
import com.voteapp.exception.VoteSecureException;
import com.voteapp.repository.OtpSessionRepository;
import com.voteapp.repository.VoterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

	private final OtpSessionRepository otpSessionRepository;
	private final VoterRepository voterRepository;
	private final JwtService jwtService;

	@Transactional
	public OtpResponse sendOtp(SendOtpRequest req) {
		// Validate voter exists
		Voter voter = voterRepository.findByVoterId(req.getVoterId())
				.orElseThrow(() -> new VoteSecureException("Voter ID not found. Please register first."));
		if (!voter.getMobileNumber().equals(req.getMobileNumber())) {
			throw new VoteSecureException("Mobile number does not match registered voter.");
		}

		// Delete old OTPs
		otpSessionRepository.deleteByMobileNumber(req.getMobileNumber());

		// Generate 6-digit OTP
		String otp = String.format("%06d", new Random().nextInt(999999));
		OtpSession session = OtpSession.builder().mobileNumber(req.getMobileNumber()).otpCode(otp).used(false).build();
		otpSessionRepository.save(session);

		// In production: send SMS via Twilio/MSG91/AWS SNS
		// For dev: log it
		log.info("OTP for {} [{}]: {}", req.getVoterId(), req.getMobileNumber(), otp);

		return OtpResponse.builder().success(true)
				// In production NEVER return OTP in response!
				// This is dev-only for testing
				.message("OTP sent to " + maskMobile(req.getMobileNumber()) + " (dev: " + otp + ")").build();
	}

	@Transactional
	public OtpResponse verifyOtp(VerifyOtpRequest req) {
		Voter voter = voterRepository.findByVoterId(req.getVoterId())
				.orElseThrow(() -> new VoteSecureException("Voter not found."));

		OtpSession session = otpSessionRepository
				.findTopByMobileNumberAndUsedFalseOrderByCreatedAtDesc(req.getMobileNumber())
				.orElseThrow(() -> new VoteSecureException("No OTP found. Please request a new one."));

		if (session.isExpired()) {
			throw new VoteSecureException("OTP has expired. Please request a new one.");
		}

		if (!session.getOtpCode().equals(req.getOtpCode())) {
			throw new VoteSecureException("Invalid OTP. Please try again.");
		}

		// Mark as used
		session.setUsed(true);
		otpSessionRepository.save(session);

		// Issue short-lived JWT session token
		String sessionToken = jwtService.generateOtpSessionToken(req.getVoterId());

		log.info("OTP verified for voter: {}", req.getVoterId());

		return OtpResponse.builder().success(true).message("OTP verified successfully.").sessionToken(sessionToken)
				.build();
	}

	private String maskMobile(String mobile) {
		if (mobile.length() <= 4)
			return mobile;
		return "****" + mobile.substring(mobile.length() - 4);
	}

}
