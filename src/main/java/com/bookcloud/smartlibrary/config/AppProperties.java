package com.bookcloud.smartlibrary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private int loanDefaultDays = 14;
	private int reservationExpiryDays = 7;
	private int maxRenewalsPerLoan = 2;
	private long overdueFinePerDayCents = 0L;
	private Frontend frontend = new Frontend();

	public int getLoanDefaultDays() {
		return loanDefaultDays;
	}

	public void setLoanDefaultDays(int loanDefaultDays) {
		this.loanDefaultDays = loanDefaultDays;
	}

	public int getReservationExpiryDays() {
		return reservationExpiryDays;
	}

	public void setReservationExpiryDays(int reservationExpiryDays) {
		this.reservationExpiryDays = reservationExpiryDays;
	}

	public int getMaxRenewalsPerLoan() {
		return maxRenewalsPerLoan;
	}

	public void setMaxRenewalsPerLoan(int maxRenewalsPerLoan) {
		this.maxRenewalsPerLoan = maxRenewalsPerLoan;
	}

	public long getOverdueFinePerDayCents() {
		return overdueFinePerDayCents;
	}

	public void setOverdueFinePerDayCents(long overdueFinePerDayCents) {
		this.overdueFinePerDayCents = overdueFinePerDayCents;
	}

	public Frontend getFrontend() {
		return frontend;
	}

	public void setFrontend(Frontend frontend) {
		this.frontend = frontend;
	}

	public static class Frontend {
		private String origin = "http://localhost:4200";

		public String getOrigin() {
			return origin;
		}

		public void setOrigin(String origin) {
			this.origin = origin;
		}
	}
}
