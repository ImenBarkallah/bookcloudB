package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateLibraryBranchRequest {

	@NotBlank
	private String name;
	private String address;
	private String openingHours;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(String openingHours) {
		this.openingHours = openingHours;
	}
}
