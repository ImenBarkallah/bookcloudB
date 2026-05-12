package com.bookcloud.smartlibrary.dto;

/** Point de série temporelle agrégée (par mois). */
public class AdminMonthlySeriesPointDto {
	private String month; // YYYY-MM
	private long loans;
	private long signups;
	private long reservations;
	private long fines;

	public AdminMonthlySeriesPointDto() {
	}

	public AdminMonthlySeriesPointDto(String month, long loans, long signups, long reservations, long fines) {
		this.month = month;
		this.loans = loans;
		this.signups = signups;
		this.reservations = reservations;
		this.fines = fines;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public long getLoans() {
		return loans;
	}

	public void setLoans(long loans) {
		this.loans = loans;
	}

	public long getSignups() {
		return signups;
	}

	public void setSignups(long signups) {
		this.signups = signups;
	}

	public long getReservations() {
		return reservations;
	}

	public void setReservations(long reservations) {
		this.reservations = reservations;
	}

	public long getFines() {
		return fines;
	}

	public void setFines(long fines) {
		this.fines = fines;
	}
}

