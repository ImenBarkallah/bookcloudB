package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.model.LibraryHistoryEntry;

public interface LibraryHistoryRepository extends JpaRepository<LibraryHistoryEntry, Long> {

	@EntityGraph(attributePaths = { "user", "book" })
	List<LibraryHistoryEntry> findByUser_UidOrderByOccurredAtDesc(String uid);

	@EntityGraph(attributePaths = { "user", "book" })
	List<LibraryHistoryEntry> findByReferenceIdOrderByOccurredAtAsc(Long referenceId);

	@EntityGraph(attributePaths = { "user", "book" })
	@Query("select h from LibraryHistoryEntry h order by h.occurredAt desc")
	List<LibraryHistoryEntry> findRecent(org.springframework.data.domain.Pageable pageable);
}
