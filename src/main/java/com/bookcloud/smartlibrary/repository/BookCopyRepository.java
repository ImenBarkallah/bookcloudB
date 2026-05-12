package com.bookcloud.smartlibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.enums.BookCopyStatus;
import com.bookcloud.smartlibrary.model.BookCopy;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

	@EntityGraph(attributePaths = { "book", "branch" })
	List<BookCopy> findByBook_IdOrderByIdAsc(Long bookId);

	@EntityGraph(attributePaths = { "book", "branch" })
	List<BookCopy> findByBook_IdAndStatusOrderByIdAsc(Long bookId, BookCopyStatus status);

	@EntityGraph(attributePaths = { "book", "branch" })
	Optional<BookCopy> findFirstByBook_IdAndStatusOrderByIdAsc(Long bookId, BookCopyStatus status);
}
