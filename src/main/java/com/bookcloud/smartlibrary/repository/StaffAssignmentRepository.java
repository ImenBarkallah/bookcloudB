package com.bookcloud.smartlibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.model.StaffAssignment;

public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {

	@EntityGraph(attributePaths = { "user", "branch" })
	List<StaffAssignment> findAllByOrderByCreatedAtDesc();

	@EntityGraph(attributePaths = { "user", "branch" })
	List<StaffAssignment> findByBranch_IdOrderByCreatedAtDesc(Long branchId);
}
