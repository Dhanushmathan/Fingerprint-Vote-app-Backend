package com.voteapp.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(AlreadyVotedException.class)
	public ResponseEntity<Map<String, Object>> handleAlreadyVoted(AlreadyVotedException ex) {
		log.warn("Already voted attempt: {}", ex.getMessage());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("error", "ALREADY_VOTED");
		body.put("message", "You have already cast your vote in this election!");
		body.put("voterId", ex.getVoterId());
		body.put("votedForCandidate", ex.getVotedForCandidate());
		body.put("votedForParty", ex.getVotedForParty());
		body.put("votedAt", ex.getVotedAt());
		body.put("timestamp", LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	@ExceptionHandler(VoteSecureException.class)
	public ResponseEntity<Map<String, Object>> handleVoteSecure(VoteSecureException ex) {
		log.warn("VoteSecure error: {}", ex.getMessage());
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("error", "VOTE_SECURE_ERROR");
		body.put("message", ex.getMessage());
		body.put("timestamp", LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).collect(Collectors.toList());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("error", "VALIDATION_ERROR");
		body.put("messages", errors);
		body.put("timestamp", LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
		log.error("Unexpected error: ", ex);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("error", "INTERNAL_ERROR");
		body.put("message", "An unexpected error occurred. Please try again.");
		body.put("timestamp", LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}
}
