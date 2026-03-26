package com.voteapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voteapp.dto.ResultDTOs;
import com.voteapp.dto.VoteDTOs;
import com.voteapp.service.VoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VoteController {

	private final VoteService voteService;

	@PostMapping("/cast")
	public ResponseEntity<VoteDTOs.VoteResponse> castVote(@Valid @RequestBody VoteDTOs.CaseVoteRequest req) {
		return ResponseEntity.ok(voteService.caseVote(req));
	}

	@GetMapping("/results")
	public ResponseEntity<ResultDTOs.ElectionResult> getResults() {
		return ResponseEntity.ok(voteService.getResults());
	}
}
