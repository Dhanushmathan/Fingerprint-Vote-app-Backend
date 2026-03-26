package com.voteapp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class VoterDTOs {
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class RegisterVoterRequest {
		@NotBlank
		private String voterId;

		@NotBlank
		private String fullName;

		@NotBlank
		@Pattern(regexp = "^[+]?[0-9]{10,13}$")
		private String mobileNumber;
		private String email;
		private String ward;

		// Fingerprint token captured during registration — hashed and stored in DB
		@NotBlank(message = "Fingerprint scan is required for registration")
		private String fingerprintToken;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class VoterResponse {
		private Long id;
		private String voterId;
		private String fullName;
		private String mobileNumber;
		private String ward;
		private Boolean hasVoted;
		private Boolean fingerprintEnrolled;
		private String votedForCandidateName;
		private String votedForPartyName;
		private LocalDateTime votedAt;
		private LocalDateTime registeredAt;
	}
}
