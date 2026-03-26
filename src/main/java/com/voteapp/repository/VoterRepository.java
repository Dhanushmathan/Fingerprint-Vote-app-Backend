package com.voteapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voteapp.entity.Voter;

public interface VoterRepository extends JpaRepository<Voter, Long> {

	Optional<Voter> findByVoterId(String voterId);

	@Query("SELECT v FROM Voter v LEFT JOIN FETCH v.votedForCandidate c LEFT JOIN FETCH c.party WHERE v.voterId = :voterId")
	Optional<Voter> findByVoterIdWithCandidate(@Param("voterId") String voterId);

	Optional<Voter> findByMobileNumber(String mobileNumber);

	boolean existsByVoterId(String voterId);

	boolean existsByMobileNumber(String mobileNumber);

	long countByHasVotedTrue();

	long countByHasVotedFalse();

	@Query("SELECT SUM(v.blockedAttempts) FROM Voter v")
	Long getTotalBlockedAttempts();

}
