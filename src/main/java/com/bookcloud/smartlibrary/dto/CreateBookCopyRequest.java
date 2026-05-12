package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.NotNull;

public class CreateBookCopyRequest {

	@NotNull
	private Long branchId;
	private String barcode;

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
}
