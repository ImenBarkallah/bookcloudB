package com.bookcloud.smartlibrary.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.enums.OfferType;
import com.bookcloud.smartlibrary.model.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {

	@EntityGraph(attributePaths = { "relatedBooks", "categories" })
	@Query("""
			select o from Offer o
			where (:search is null or
			       lower(coalesce(o.title, '')) like lower(concat('%', cast(:search as string), '%')) or
			       lower(coalesce(o.description, '')) like lower(concat('%', cast(:search as string), '%')))
			  and (:type is null or o.type = :type)
			  and (:active is null or o.active = :active)
			order by o.createdAt desc
			""")
	Page<Offer> searchAdmin(
			@Param("search") String search,
			@Param("type") OfferType type,
			@Param("active") Boolean active,
			Pageable pageable);

	@EntityGraph(attributePaths = { "relatedBooks", "categories" })
	List<Offer> findByActiveTrueOrderByCreatedAtDesc();

	@EntityGraph(attributePaths = { "relatedBooks", "categories" })
	List<Offer> findAllByOrderByCreatedAtDesc();

	@EntityGraph(attributePaths = { "relatedBooks", "categories" })
	List<Offer> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByCreatedAtDesc(
			LocalDate startDate, LocalDate endDate);
}
