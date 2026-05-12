package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.Min;

public class UpdateLibrarySettingsRequest {
	@Min(1)
	private Integer defaultLoanDays;
	@Min(0)
	private Integer maxActiveLoansDefault;
	@Min(0)
	private Integer reservationExpiryDays;
	@Min(0)
	private Double finePerDay;

	public Integer getDefaultLoanDays() {
		return defaultLoanDays;
	}

	public void setDefaultLoanDays(Integer defaultLoanDays) {
		this.defaultLoanDays = defaultLoanDays;
	}

	public Integer getMaxActiveLoansDefault() {
		return maxActiveLoansDefault;
	}

	public void setMaxActiveLoansDefault(Integer maxActiveLoansDefault) {
		this.maxActiveLoansDefault = maxActiveLoansDefault;
	}

	public Integer getReservationExpiryDays() {
		return reservationExpiryDays;
	}

	public void setReservationExpiryDays(Integer reservationExpiryDays) {
		this.reservationExpiryDays = reservationExpiryDays;
	}

	public Double getFinePerDay() {
		return finePerDay;
	}

	public void setFinePerDay(Double finePerDay) {
		this.finePerDay = finePerDay;
	}
}

