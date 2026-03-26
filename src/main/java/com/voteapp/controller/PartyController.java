package com.voteapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voteapp.dto.PartyDtos;
import com.voteapp.service.PartyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parties")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PartyController {

	private final PartyService partyService;

	@PostMapping
	public ResponseEntity<PartyDtos.PartyResponse> register(@Valid @RequestBody PartyDtos.CreatePartyRequest req) {
		return ResponseEntity.status(HttpStatus.CREATED).body(partyService.registerParty(req));
	}

	@GetMapping
	public ResponseEntity<List<PartyDtos.PartyResponse>> getAll() {
		return ResponseEntity.ok(partyService.getAllParties());
	}

	@GetMapping("/{id}")
	public ResponseEntity<PartyDtos.PartyResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(partyService.getPartyById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		partyService.deleteParty(id);
		return ResponseEntity.noContent().build();
	}

}
