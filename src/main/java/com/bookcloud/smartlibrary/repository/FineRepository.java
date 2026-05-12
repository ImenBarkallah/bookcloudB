package com.bookcloud.smartlibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.enums.FineStatus;
import com.bookcloud.smartlibrary.model.Fine;

public interface FineRepository extends JpaRepository<Fine, Long> {

	@EntityGraph(attributePaths = { "loan", "user" })
	List<Fine> findByUser_UidOrderByCreatedAtDesc(String uid);

	@EntityGraph(attributePaths = { "loan", "user" })
	Optional<Fine> findFirstByLoan_IdAndStatus(Long loanId, FineStatus status);
}
