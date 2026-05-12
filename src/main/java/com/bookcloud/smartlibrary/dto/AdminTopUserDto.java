package com.bookcloud.smartlibrary.dto;

/** Utilisateur "top" (activité) pour dashboard admin. */
public class AdminTopUserDto {
	private String userUid;
	private String displayName;
	private String email;
	private long totalLoans;
	private long activeLoans;

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getTotalLoans() {
		return totalLoans;
	}

	public void setTotalLoans(long totalLoans) {
		this.totalLoans = totalLoans;
	}

	public long getActiveLoans() {
		return activeLoans;
	}

	public void setActiveLoans(long activeLoans) {
		this.activeLoans = activeLoans;
	}
}

