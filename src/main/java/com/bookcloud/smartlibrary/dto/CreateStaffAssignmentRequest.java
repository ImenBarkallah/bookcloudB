package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateStaffAssignmentRequest {

	@NotBlank
	private String userUid;
	/** optionnel : id d’antenne {@code libraryBranches} */
	private Long branchId;
	@NotBlank
	private String staffRole;
	private boolean active = true;

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getStaffRole() {
		return staffRole;
	}

	public void setStaffRole(String staffRole) {
		this.staffRole = staffRole;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

