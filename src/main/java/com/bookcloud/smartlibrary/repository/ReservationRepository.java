package com.bookcloud.smartlibrary.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.enums.ReservationStatus;
import com.bookcloud.smartlibrary.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@EntityGraph(attributePaths = { "book", "user", "pickupBranch" })
	List<Reservation> findByUser_UidOrderByCreatedAtDesc(String uid);

	@EntityGraph(attributePaths = { "book", "user", "pickupBranch" })
	List<Reservation> findByBook_IdAndStatusOrderByCreatedAtAsc(Long bookId, ReservationStatus status);

	@EntityGraph(attributePaths = { "book", "user", "pickupBranch" })
	@Query("""
			select r from Reservation r
			where r.user.uid = :uid
			  and r.book.id = :bookId
			  and r.status in :statuses
			order by r.createdAt desc
			""")
	Optional<Reservation> findFirstByUserUidAndBookIdAndStatusIn(
			@Param("uid") String uid,
			@Param("bookId") Long bookId,
			@Param("statuses") Collection<ReservationStatus> statuses);

	long countByBook_IdAndStatus(Long bookId, ReservationStatus status);

	@EntityGraph(attributePaths = { "book", "user", "pickupBranch" })
	List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant expiresAt);

	@EntityGraph(attributePaths = { "book", "user", "pickupBranch" })
	@Query("select r from Reservation r order by r.createdAt desc")
	List<Reservation> findAllDetailed();
}
