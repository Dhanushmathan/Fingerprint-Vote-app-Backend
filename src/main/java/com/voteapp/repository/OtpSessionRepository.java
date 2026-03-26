package com.voteapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voteapp.entity.OtpSession;

public interface OtpSessionRepository extends JpaRepository<OtpSession, Long> {
	Optional<OtpSession> findTopByMobileNumberAndUsedFalseOrderByCreatedAtDesc(String mobileNumber);

	void deleteByMobileNumber(String mobileNumber);
}
