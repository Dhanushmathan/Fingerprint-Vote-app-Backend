package com.voteapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voteapp.dto.OtpDTOs;
import com.voteapp.service.OtpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OtpController {

	private final OtpService otpService;

	@PostMapping("/send")
	public ResponseEntity<OtpDTOs.OtpResponse> send(@Valid @RequestBody OtpDTOs.SendOtpRequest req) {
		return ResponseEntity.ok(otpService.sendOtp(req));
	}

	@PostMapping("/verify")
	public ResponseEntity<OtpDTOs.OtpResponse> verify(@Valid @RequestBody OtpDTOs.VerifyOtpRequest req) {
		return ResponseEntity.ok(otpService.verifyOtp(req));
	}
}
