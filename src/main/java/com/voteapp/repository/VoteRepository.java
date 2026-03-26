package com.voteapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voteapp.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
	boolean existsByVoterId(Long voterId);

	Optional<Vote> findByVoterId(Long voterId);

	List<Vote> findAllByOrderByCastedAtDesc();
}
