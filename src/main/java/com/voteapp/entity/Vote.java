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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "votes", uniqueConstraints = {
		@UniqueConstraint(name = "uk_votes_voter_id", columnNames = { "voter_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voter_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Voter voter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "candidate_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Candidate candidate;

	// Blockchain style transaction hash
	@Column(unique = true, nullable = false)
	private String txHash;

	@Column(updatable = false, nullable = false)
	private LocalDateTime castedAt;

	// Auth methods used
	private Boolean otpVerified = false;
	private Boolean fingerprintVerified = false;

	@PrePersist
	protected void onCreate() {
		castedAt = LocalDateTime.now();
	}
}
