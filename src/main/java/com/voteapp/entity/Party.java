package com.voteapp.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "parties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Party {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String leaderName;

	@Column(length = 10)
	private String symbol;

	private String colorTheme;
	private String manifesto;
	private Integer foundedYear;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PartyStatus status = PartyStatus.ACTIVE;

	@Column(updatable = false)
	private LocalDateTime registeredAt;

	@OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Candidate> candidates = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		registeredAt = LocalDateTime.now();
	}

	public enum PartyStatus {
		ACTIVE, SUSPENDED, DISQUALIFIED
	}
}
