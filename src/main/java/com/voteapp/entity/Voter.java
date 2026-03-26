package com.voteapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "voters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String voterId;

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String mobileNumber;

	@Column(unique = true)
	private String email;

	private String ward;

	// Fingerprint template hash (in real system store encrypted biometric hash)
	private String fingerprintHash;

	@Column(nullable = false)
	private Boolean hasVoted = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voted_for_candidate_id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Candidate votedForCandidate;

	private LocalDateTime votedAt;

	@Column(nullable = false)
	private Integer blockedAttempts = 0;

	@Column(updatable = false)
	private LocalDateTime registeredAt;

	@PrePersist
	protected void onCreate() {
		registeredAt = LocalDateTime.now();
		if (hasVoted == null)
			hasVoted = false;
		if (blockedAttempts == null)
			blockedAttempts = 0;
	}

}
