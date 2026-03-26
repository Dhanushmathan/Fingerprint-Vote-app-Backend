package com.voteapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voteapp.dto.CandidateDTOs.CandidateResponse;
import com.voteapp.dto.PartyDtos.CreatePartyRequest;
import com.voteapp.dto.PartyDtos.PartyResponse;
import com.voteapp.entity.Candidate;
import com.voteapp.entity.Party;
import com.voteapp.exception.VoteSecureException;
import com.voteapp.repository.CandidateRepository;
import com.voteapp.repository.PartyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyService {

	private final PartyRepository partyRepository;
	private final CandidateRepository candidateRepository;

	@Transactional
	public PartyResponse registerParty(CreatePartyRequest req) {
		if (partyRepository.existsByName(req.getName())) {
			throw new VoteSecureException("Party with name '" + req.getName() + "' already exists!");
		}

		Party party = Party.builder().name(req.getName()).leaderName(req.getLeaderName()).symbol(req.getSymbol())
				.colorTheme(req.getColorTheme()).manifesto(req.getManifesto()).foundedYear(req.getFoundedYear())
				.status(Party.PartyStatus.ACTIVE).build();
		party = partyRepository.save(party);
		log.info("Party registered: {}", party.getName());

		// Register Candidates
		final Party savedParty = party;
		List<Candidate> candidates = req.getCandidates().stream()
				.map(c -> Candidate.builder().name(c.getName()).age(c.getAge()).qualifications(c.getQualifications())
						.ward(c.getWard() != null ? c.getWard() : "Ward 7").party(savedParty).voteCount(0L).build())
				.collect(Collectors.toList());
		candidateRepository.saveAll(candidates);
		party.setCandidates(candidates);

		return mapToResponse(party);
	}

	public List<PartyResponse> getAllParties() {
		return partyRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	public PartyResponse getPartyById(Long id) {
		Party party = partyRepository.findByIdWithCandidates(id)
				.orElseThrow(() -> new VoteSecureException("Party not found with id: " + id));
		return mapToResponse(party);
	}

	@Transactional
	public void deleteParty(Long id) {
		Party party = partyRepository.findById(id)
				.orElseThrow(() -> new VoteSecureException("Party not found with id: " + id));
		partyRepository.delete(party);
		log.info("Party deleted: {}", party.getName());
	}

	private PartyResponse mapToResponse(Party party) {
		Long totalVotes = party.getCandidates() == null ? 0
				: party.getCandidates().stream().mapToLong(c -> c.getVoteCount() == null ? 0 : c.getVoteCount()).sum();

		List<CandidateResponse> candidateResponsces = party.getCandidates() == null ? List.of()
				: party.getCandidates().stream().map(c -> mapCandidateToResponse(c, null)).collect(Collectors.toList());

		return PartyResponse.builder().id(party.getId()).name(party.getName()).leaderName(party.getLeaderName())
				.symbol(party.getSymbol()).colorTheme(party.getColorTheme()).manifesto(party.getManifesto())
				.foundedYear(party.getFoundedYear()).status(party.getStatus().name())
				.registeredAt(party.getRegisteredAt()).candidates(candidateResponsces).totalVotes(totalVotes).build();
	}

	public static CandidateResponse mapCandidateToResponse(Candidate c, Long totalVotes) {

		long voteCount = c.getVoteCount() == null ? 0 : c.getVoteCount();
		double pct = 0.0;
		if (totalVotes != null && totalVotes > 0) {
			pct = (voteCount * 100.0) / totalVotes;
			pct = Math.min(pct, 100.0);
		}

		return CandidateResponse.builder().id(c.getId()).age(c.getAge()).name(c.getName())
				.qualifications(c.getQualifications()).ward(c.getWard()).voteCount(c.getVoteCount())
				.votePercentage(Math.round(pct * 10.0) / 10.0)
				.partyName(c.getParty() != null ? c.getParty().getName() : null)
				.partySymbol(c.getParty() != null ? c.getParty().getSymbol() : null)
				.partyColorTheme(c.getParty() != null ? c.getParty().getColorTheme() : null)
				.registeredAt(c.getRegisteredAt()).build();
	}

}
