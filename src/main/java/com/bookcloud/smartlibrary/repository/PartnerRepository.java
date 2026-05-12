package com.bookcloud.smartlibrary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.model.Partner;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

	java.util.List<Partner> findByArchivedFalseOrderByNameAsc();

	java.util.List<Partner> findAllByOrderByNameAsc();

	@Query("""
			select p from Partner p
			where (:includeArchived = true or p.archived = false)
			  and (:search is null or
			       lower(coalesce(p.name, '')) like lower(concat('%', cast(:search as string), '%')) or
			       lower(coalesce(p.tier, '')) like lower(concat('%', cast(:search as string), '%')))
			order by p.name asc
			""")
	Page<Partner> search(@Param("search") String search, @Param("includeArchived") boolean includeArchived, Pageable pageable);
}
