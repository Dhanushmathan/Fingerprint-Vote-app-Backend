package com.voteapp.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PartyDtos {
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CreatePartyRequest {
		@NotBlank(message = "Party name is required")
		private String name;

		@NotBlank(message = "Leader name is required")
		private String leaderName;

		private String symbol;
		private String colorTheme;
		private String manifesto;
		private Integer foundedYear;

		@NotEmpty(message = "At least one candidate is required")
		private List<CandidateDTOs.CreateCandidateRequest> candidates;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class PartyResponse {
		private Long id;
		private String name;
		private String leaderName;
		private String symbol;
		private String colorTheme;
		private String manifesto;
		private Integer foundedYear;
		private String status;
		private LocalDateTime registeredAt;
		private List<CandidateDTOs.CandidateResponse> candidates;
		private Long totalVotes;
	}
}
