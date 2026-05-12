package com.bookcloud.smartlibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.model.FavoriteBook;

public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {

	Optional<FavoriteBook> findByUser_UidAndBook_Id(String userUid, Long bookId);

	void deleteByUser_UidAndBook_Id(String userUid, Long bookId);

	@Query("""
			select fb.book.id
			from FavoriteBook fb
			where fb.user.uid = :uid
			order by fb.createdAt desc, fb.id desc
			""")
	List<Long> findFavoriteBookIdsByUserUid(@Param("uid") String uid);

	@Query("""
			select count(fb)
			from FavoriteBook fb
			where fb.user.uid = :uid
			""")
	long countByUserUid(@Param("uid") String uid);

	@Query("""
			select fb.book.id, count(fb)
			from FavoriteBook fb
			group by fb.book.id
			""")
	List<Object[]> countByBookId();
}
