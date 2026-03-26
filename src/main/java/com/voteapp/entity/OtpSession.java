package com.voteapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String mobileNumber;

	@Column(nullable = false)
	private String otpCode;

	@Column(nullable = false)
	private LocalDateTime expriesAt;

	private Boolean used = false;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		// OTP valid for 10 minutes
		expriesAt = LocalDateTime.now().plusMinutes(10);
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expriesAt);
	}
}
