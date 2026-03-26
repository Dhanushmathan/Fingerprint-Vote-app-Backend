package com.voteapp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ResultDTOs {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ElectionResult {
		private Long totalVotes;
		private Long totalRegisteredVoters;
		private Double turnoutPercentage;
		private Long totalParties;
		private Long totalCandidates;
		private Long blockedAttempts;
		private List<CandidateDTOs.CandidateResponse> rankings;
	}
}
