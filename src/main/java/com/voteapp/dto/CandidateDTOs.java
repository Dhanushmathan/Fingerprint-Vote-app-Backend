package com.voteapp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CandidateDTOs {
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CreateCandidateRequest {
		@NotBlank(message = "Candidate name is required")
		private String name;

		@Min(25)
		@Max(80)
		private Integer age;

		private String qualifications;
		private String ward;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CandidateResponse {
		private Long id;
		private String name;
		private Integer age;
		private String qualifications;
		private String ward;
		private Long voteCount;
		private Double votePercentage;
		private String partyName;
		private String partySymbol;
		private String partyColorTheme;
		private LocalDateTime registeredAt;
	}
}
