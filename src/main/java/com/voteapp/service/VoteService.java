package com.voteapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voteapp.dto.CandidateDTOs.CandidateResponse;
import com.voteapp.dto.ResultDTOs.ElectionResult;
import com.voteapp.dto.VoteDTOs.CaseVoteRequest;
import com.voteapp.dto.VoteDTOs.VoteResponse;
import com.voteapp.entity.Candidate;
import com.voteapp.entity.Vote;
import com.voteapp.entity.Voter;
import com.voteapp.exception.AlreadyVotedException;
import com.voteapp.exception.VoteSecureException;
import com.voteapp.repository.CandidateRepository;
import com.voteapp.repository.VoteRepository;
import com.voteapp.repository.VoterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

	private final VoteRepository voteRepository;
	private final VoterRepository voterRepository;
	private final CandidateRepository candidateRepository;
	private final JwtService jwtService;
	private final FingerprintService fingerprintService;

	/*
	 * Cast a vote — enforces ONE vote per voter strictly. Throws
	 * AlreadyVotedException if voter has already voted.
	 */

	@Transactional
	public VoteResponse caseVote(CaseVoteRequest req) {

		// ── 1. Validate session token (OTP must have been verified) ──
		if (!jwtService.validateOtpSessionToken(req.getOtpSessionToken(), req.getVoterId())) {
			throw new VoteSecureException("Invalid or expired session. Please verify OTP again.");
		}

		// ── 2. Load voter ──
		Voter voter = voterRepository.findByVoterId(req.getVoterId())
				.orElseThrow(() -> new VoteSecureException("Voter not found: " + req.getVoterId()));

		// ── 3. STRICT CHECK: Has this voter already voted? ──
		if (Boolean.TRUE.equals(voter.getHasVoted())) {
			// Increment blocked attempts counter
			voter.setBlockedAttempts(voter.getBlockedAttempts() + 1);
			voterRepository.save(voter);

			String alreadyVotedFor = voter.getVotedForCandidate() != null ? voter.getVotedForCandidate().getName()
					: "Unknown";

			log.warn("BLOCKED VOTE: Voter {} tried to vote again (already voted for {})", req.getVoterId(),
					alreadyVotedFor);

			throw new AlreadyVotedException(voter.getVoterId(), alreadyVotedFor,
					voter.getVotedForCandidate() != null && voter.getVotedForCandidate().getParty() != null
							? voter.getVotedForCandidate().getParty().getName()
							: "",
					voter.getVotedAt());
		}

		// ── 4. Load candidate ──
		Candidate candidate = candidateRepository.findById(req.getCandidateId())
				.orElseThrow(() -> new VoteSecureException("Candidate not found: " + req.getCandidateId()));

		// ── 5. Validate fingerprint against stored DB hash ──
		if (req.getFingerprintToken() == null || req.getFingerprintToken().isBlank()) {
			throw new VoteSecureException("Fingerprint verification required.");
		}
		if (voter.getFingerprintHash() == null || voter.getFingerprintHash().isBlank()) {
			throw new VoteSecureException("Voter fingerprint not enrolled! Please re-register with fingerprint scan.");
		}
		boolean fpMatch = fingerprintService.verifyFingerprint(req.getFingerprintToken(), voter.getFingerprintHash());
		if (!fpMatch) {
			voter.setBlockedAttempts(voter.getBlockedAttempts() + 1);
			voterRepository.save(voter);
			throw new VoteSecureException("Fingerprint mismatch! Biometric verification failed. Access denied.");
		}
		log.info("Fingerprint VERIFIED for voter: {}", req.getVoterId());

		// ── 6. Record the vote ──
		String txHash = "0x" + UUID.randomUUID().toString().replace("-", "");

		Vote vote = Vote.builder().voter(voter).candidate(candidate).txHash(txHash).otpVerified(true)
				.fingerprintVerified(true).build();
		voteRepository.save(vote);

		// ── 7. Update candidate vote count ──
		candidate.setVoteCount(candidate.getVoteCount() + 1);
		candidateRepository.save(candidate);

		// ── 8. Mark voter as voted — LOCKED ──
		voter.setHasVoted(true);
		voter.setVotedForCandidate(candidate);
		voter.setVotedAt(LocalDateTime.now());
		voterRepository.save(voter);

		jwtService.consumeToken(req.getOtpSessionToken());
		log.info("VOTE CAST: {} voted for {} ({})", req.getVoterId(), candidate.getName(),
				candidate.getParty().getName());

		return VoteResponse.builder().success(true).message("Vote cast successfully!").txHash(txHash)
				.candidateName(candidate.getName()).partyName(candidate.getParty().getName())
				.castedAt(voter.getVotedAt()).build();
	}

	/* Get full election results with rankings. */

	@Transactional(readOnly = true)
	public ElectionResult getResults() {

		List<Candidate> candidates = candidateRepository.findAllWithPartyOrderByVotes();
		long totalRegistered = voterRepository.count();
		long voted = voterRepository.countByHasVotedTrue();
		Long blocked = voterRepository.getTotalBlockedAttempts();
		if (blocked == null)
			blocked = 0L;

		final long tv = voted;
		List<CandidateResponse> rankings = candidates.stream().map(c -> PartyService.mapCandidateToResponse(c, tv))
				.collect(Collectors.toList());

		double turnout = totalRegistered > 0 ? (voted * 100.0 / totalRegistered) : 0;

		return ElectionResult.builder().totalVotes(tv).totalRegisteredVoters(totalRegistered)
				.turnoutPercentage(Math.round(turnout * 10.0) / 10.0)
				.totalParties((long) candidates.stream().map(c -> c.getParty().getId()).distinct().count())
				.totalCandidates((long) candidates.size()).blockedAttempts(blocked).rankings(rankings).build();
	}

}
