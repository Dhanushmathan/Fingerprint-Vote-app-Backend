package com.voteapp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class VoteDTOs {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CaseVoteRequest {
		@NotBlank
		private String voterId;
		@NotNull
		private Long candidateId;
		@NotBlank
		private String fingerprintToken; // simulated FP token
		@NotBlank
		private String otpSessionToken;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class VoteResponse {
		private Boolean success;
		private String message;
		private String txHash;
		private String candidateName;
		private String partyName;
		private LocalDateTime castedAt;
	}

}
