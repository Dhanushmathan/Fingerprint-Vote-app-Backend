package com.voteapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voteapp.entity.Party;

public interface PartyRepository extends JpaRepository<Party, Long> {

	Optional<Party> findByName(String name);

	boolean existsByName(String name);

	List<Party> findByStatus(Party.PartyStatus status);

	@Query("SELECT p FROM Party p LEFT JOIN FETCH p.candidates WHERE p.id = :id")
	Optional<Party> findByIdWithCandidates(@Param("id") Long id);
}
