package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bookcloud.smartlibrary.model.TransactionRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

	@EntityGraph(attributePaths = { "user", "book" })
	List<TransactionRecord> findByUser_UidOrderByOccurredAtDesc(String uid);

	@EntityGraph(attributePaths = { "user", "book" })
	@Query("select t from TransactionRecord t order by t.occurredAt desc")
	List<TransactionRecord> findAllDetailed();
}
