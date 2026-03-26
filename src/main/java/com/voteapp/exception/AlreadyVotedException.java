package com.voteapp.exception;

import java.time.LocalDateTime;

public class AlreadyVotedException extends RuntimeException {
	private final String voterId;
	private final String votedForCandidate;
	private final String votedForParty;
	private final LocalDateTime votedAt;

	public AlreadyVotedException(String voterId, String votedForCandidate, String votedForParty,
			LocalDateTime votedAt) {
		super("Voter " + voterId + " has already voted for " + votedForCandidate);
		this.voterId = voterId;
		this.votedForCandidate = votedForCandidate;
		this.votedForParty = votedForParty;
		this.votedAt = votedAt;
	}

	public String getVoterId() {
		return voterId;
	}

	public String getVotedForCandidate() {
		return votedForCandidate;
	}

	public String getVotedForParty() {
		return votedForParty;
	}

	public LocalDateTime getVotedAt() {
		return votedAt;
	}
}
