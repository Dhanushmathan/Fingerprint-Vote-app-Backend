package com.voteapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voteapp.dto.VoterDTOs;
import com.voteapp.service.VoterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/voters")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VoterController {

	private final VoterService voterService;

	@PostMapping("/register")
	public ResponseEntity<VoterDTOs.VoterResponse> register(@Valid @RequestBody VoterDTOs.RegisterVoterRequest req) {
		return ResponseEntity.status(HttpStatus.CREATED).body(voterService.registerVoter(req));
	}

	@GetMapping("/{voterId}")
	public ResponseEntity<VoterDTOs.VoterResponse> getVoter(@PathVariable String voterId) {
		return ResponseEntity.ok(voterService.getVoterByVoterId(voterId));
	}

	@GetMapping
	public ResponseEntity<List<VoterDTOs.VoterResponse>> getAll() {
		return ResponseEntity.ok(voterService.getAllVoters());
	}

}
