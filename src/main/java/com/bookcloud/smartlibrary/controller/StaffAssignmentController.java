package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreateStaffAssignmentRequest;
import com.bookcloud.smartlibrary.dto.UpdateStaffAssignmentRequest;
import com.bookcloud.smartlibrary.model.StaffAssignment;
import com.bookcloud.smartlibrary.service.StaffAssignmentService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff-assignments")
public class StaffAssignmentController {

	private final StaffAssignmentService staffAssignmentService;

	public StaffAssignmentController(StaffAssignmentService staffAssignmentService) {
		this.staffAssignmentService = staffAssignmentService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<StaffAssignment> list() {
		return staffAssignmentService.listAll();
	}

	@GetMapping("/by-branch/{branchId}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<StaffAssignment> listByBranch(@PathVariable Long branchId) {
		return staffAssignmentService.listByBranch(branchId);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public StaffAssignment get(@PathVariable Long id) {
		return staffAssignmentService.getById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public StaffAssignment create(@Valid @RequestBody CreateStaffAssignmentRequest req,
			Authentication authentication) {
		return staffAssignmentService.create(req, SecurityUtils.currentRole(authentication));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public StaffAssignment update(@PathVariable Long id, @Valid @RequestBody UpdateStaffAssignmentRequest req,
			Authentication authentication) {
		return staffAssignmentService.update(id, req, SecurityUtils.currentRole(authentication));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(@PathVariable Long id, Authentication authentication) {
		staffAssignmentService.delete(id, SecurityUtils.currentRole(authentication));
	}
}
