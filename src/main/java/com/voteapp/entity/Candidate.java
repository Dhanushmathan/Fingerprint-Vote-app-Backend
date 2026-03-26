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
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;
	private Integer age;
	private String qualifications;
	private String ward;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Party party;

	@Column(nullable = false)
	private Long voteCount = 0L;

	@Column(updatable = false)
	private LocalDateTime registeredAt;

	@PrePersist
	protected void onCreate() {
		registeredAt = LocalDateTime.now();
		if (voteCount == null)
			voteCount = 0L;
	}
}
