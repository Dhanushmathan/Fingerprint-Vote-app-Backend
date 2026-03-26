package com.voteapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voteapp.dto.VoterDTOs.RegisterVoterRequest;
import com.voteapp.dto.VoterDTOs.VoterResponse;
import com.voteapp.entity.Voter;
import com.voteapp.exception.VoteSecureException;
import com.voteapp.repository.VoterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoterService {

	private final VoterRepository voterRepository;
	private final FingerprintService fingerprintService;

	@Transactional
	public VoterResponse registerVoter(RegisterVoterRequest req) {

		if (voterRepository.existsByVoterId(req.getVoterId())) {
			throw new VoteSecureException("Voter ID '" + req.getVoterId() + "' is already registered.");
		}
		if (voterRepository.existsByMobileNumber(req.getMobileNumber())) {
			throw new VoteSecureException("Mobile number already registered.");
		}

		// Hash the fingerprint token → store in DB
		String fpHash = fingerprintService.hashFingerprint(req.getFingerprintToken());
		log.info("Fingerprint enrolled for voter: {} → hash: {}...{}", req.getVoterId(), fpHash.substring(0, 8),
				fpHash.substring(fpHash.length() - 8));

		Voter voter = Voter.builder().voterId(req.getVoterId()).fullName(req.getFullName())
				.mobileNumber(req.getMobileNumber()).email(req.getEmail())
				.ward(req.getWard() != null ? req.getWard() : "Ward 7").fingerprintHash(fpHash).hasVoted(false)
				.blockedAttempts(0).build();
		voter = voterRepository.save(voter);
		log.info("Voter registered: {}", voter.getVoterId());
		return mapToResponse(voter);
	}

	public VoterResponse getVoterByVoterIdMan(String voterId) {
		Voter voter = voterRepository.findByVoterId(voterId)
				.orElseThrow(() -> new VoteSecureException("Voter not found: " + voterId));
		return mapToResponse(voter);
	}

	public VoterResponse getVoterByVoterId(String voterId) {
		Voter voter = voterRepository.findByVoterIdWithCandidate(voterId)
				.orElseThrow(() -> new VoteSecureException("Voter not found: " + voterId));
		return mapToResponse(voter);
	}

	public List<VoterResponse> getAllVoters() {
		return voterRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	private VoterResponse mapToResponse(Voter v) {
		return VoterResponse.builder().id(v.getId()).voterId(v.getVoterId()).fullName(v.getFullName())
				.mobileNumber(v.getMobileNumber()).ward(v.getWard()).hasVoted(v.getHasVoted())
				.votedForCandidateName(v.getVotedForCandidate() != null ? v.getVotedForCandidate().getName() : null)
				.votedForPartyName(v.getVotedForCandidate() != null && v.getVotedForCandidate().getParty() != null
						? v.getVotedForCandidate().getParty().getName()
						: null)
				.votedAt(v.getVotedAt()).registeredAt(v.getRegisteredAt()).build();
	}

}
