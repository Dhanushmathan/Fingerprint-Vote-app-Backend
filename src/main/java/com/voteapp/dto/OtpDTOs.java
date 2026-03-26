package com.voteapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OtpDTOs {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SendOtpRequest {
		@NotBlank
		private String voterId;
		@NotBlank
		private String mobileNumber;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class VerifyOtpRequest {
		@NotBlank
		private String voterId;
		@NotBlank
		private String mobileNumber;
		@NotBlank
		@Size(min = 4, max = 6)
		private String otpCode;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class OtpResponse {
		private Boolean success;
		private String message;
		private String sessionToken;
	}
}
