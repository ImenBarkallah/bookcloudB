package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.NotNull;

public class CreateReservationRequest {

	@NotNull
	private Long bookId;
	private Long pickupBranchId;

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public Long getPickupBranchId() {
		return pickupBranchId;
	}

	public void setPickupBranchId(Long pickupBranchId) {
		this.pickupBranchId = pickupBranchId;
	}
}

