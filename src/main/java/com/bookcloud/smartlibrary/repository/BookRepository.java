package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	@EntityGraph(attributePaths = { "authors", "category", "defaultBranch" })
	List<Book> findAllByOrderByCreatedAtDesc();

	@EntityGraph(attributePaths = { "authors", "category", "defaultBranch" })
	List<Book> findByCategory_Id(Long categoryId);

	@EntityGraph(attributePaths = { "authors", "category", "defaultBranch" })
	List<Book> findByAuthors_Id(Long authorId);

	@EntityGraph(attributePaths = { "authors", "category", "defaultBranch" })
	List<Book> findByFeaturedTrueAndHiddenFalse();

	@EntityGraph(attributePaths = { "authors", "category", "defaultBranch" })
	@Query("""
			select distinct b from Book b
			left join b.category c
			left join b.defaultBranch br
			where (:categoryId is null or c.id = :categoryId)
			  and (:branchId is null or br.id = :branchId)
			  and (:hidden is null or b.hidden = :hidden)
			  and (:keyword is null or trim(:keyword) = '' or
			       lower(coalesce(b.title, '')) like lower(concat('%', :keyword, '%')) or
			       lower(coalesce(b.description, '')) like lower(concat('%', :keyword, '%')) or
			       lower(coalesce(b.author, '')) like lower(concat('%', :keyword, '%')))
			""")
	Page<Book> searchCatalog(
			@Param("categoryId") Long categoryId,
			@Param("branchId") Long branchId,
			@Param("keyword") String keyword,
			@Param("hidden") Boolean hidden,
			Pageable pageable);

	@EntityGraph(attributePaths = { "authors", "category", "defaultBranch" })
	@Query("""
			select b from Book b
			where b.category.id = :categoryId
			  and b.id <> :bookId
			  and b.hidden = false
			""")
	List<Book> findSimilarBooks(@Param("categoryId") Long categoryId, @Param("bookId") Long bookId, Pageable pageable);

	long countByHidden(boolean hidden);
}
