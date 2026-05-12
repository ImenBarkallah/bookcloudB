package com.bookcloud.smartlibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookcloud.smartlibrary.model.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByUid(String uid);

	Optional<AppUser> findByEmail(String email);
	@Query("""
			select u from AppUser u
			where (:search is null or
			       lower(coalesce(u.email, '')) like lower(concat('%', :search, '%')) or
			       lower(coalesce(u.displayName, '')) like lower(concat('%', :search, '%')))
			""")
	Page<AppUser> searchAdminUsers(@Param("search") String search, Pageable pageable);
}
