package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.CreateStaffAssignmentRequest;
import com.bookcloud.smartlibrary.dto.UpdateStaffAssignmentRequest;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.StaffAssignment;

public interface StaffAssignmentService {
	List<StaffAssignment> listAll();
	List<StaffAssignment> listByBranch(Long branchId);
	StaffAssignment getById(Long id);
	StaffAssignment create(CreateStaffAssignmentRequest req, Role role);
	StaffAssignment update(Long id, UpdateStaffAssignmentRequest req, Role role);
	void delete(Long id, Role role);
}
