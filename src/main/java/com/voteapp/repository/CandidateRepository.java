package com.voteapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voteapp.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
	List<Candidate> findByPartyId(Long partyId);

	List<Candidate> findByWard(String ward);

	@Query("SELECT c FROM Candidate c JOIN FETCH c.party ORDER BY c.voteCount DESC")
	List<Candidate> findAllWithPartyOrderByVotes();

	@Query("SELECT SUM(c.voteCount) FROM Candidate c")
	Long getTotalVotes();
}
