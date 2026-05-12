package com.bookcloud.smartlibrary.dto;

/** KPI essentiels pour le dashboard admin. */
public class AdminDashboardOverviewDto {

	// Livres
	private long totalBooks;
	private long totalCopies;
	private long availableCopies;
	private long borrowedCopies;

	// Utilisateurs
	private long totalUsers;
	private long newUsersThisMonth;
	private long activeUsers;

	// Emprunts
	private long activeLoans;
	private long overdueLoans;
	private long loansToday;

	// Réservations
	private long pendingReservations;
	private long approvedReservations;

	// Amendes
	private long totalFines;
	private long openFines;
	private long totalFineAmountCents;
	private long usersWithDebt;

	public long getTotalBooks() {
		return totalBooks;
	}

	public void setTotalBooks(long totalBooks) {
		this.totalBooks = totalBooks;
	}

	public long getTotalCopies() {
		return totalCopies;
	}

	public void setTotalCopies(long totalCopies) {
		this.totalCopies = totalCopies;
	}

	public long getAvailableCopies() {
		return availableCopies;
	}

	public void setAvailableCopies(long availableCopies) {
		this.availableCopies = availableCopies;
	}

	public long getBorrowedCopies() {
		return borrowedCopies;
	}

	public void setBorrowedCopies(long borrowedCopies) {
		this.borrowedCopies = borrowedCopies;
	}

	public long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public long getNewUsersThisMonth() {
		return newUsersThisMonth;
	}

	public void setNewUsersThisMonth(long newUsersThisMonth) {
		this.newUsersThisMonth = newUsersThisMonth;
	}

	public long getActiveUsers() {
		return activeUsers;
	}

	public void setActiveUsers(long activeUsers) {
		this.activeUsers = activeUsers;
	}

	public long getActiveLoans() {
		return activeLoans;
	}

	public void setActiveLoans(long activeLoans) {
		this.activeLoans = activeLoans;
	}

	public long getOverdueLoans() {
		return overdueLoans;
	}

	public void setOverdueLoans(long overdueLoans) {
		this.overdueLoans = overdueLoans;
	}

	public long getLoansToday() {
		return loansToday;
	}

	public void setLoansToday(long loansToday) {
		this.loansToday = loansToday;
	}

	public long getPendingReservations() {
		return pendingReservations;
	}

	public void setPendingReservations(long pendingReservations) {
		this.pendingReservations = pendingReservations;
	}

	public long getApprovedReservations() {
		return approvedReservations;
	}

	public void setApprovedReservations(long approvedReservations) {
		this.approvedReservations = approvedReservations;
	}

	public long getTotalFines() {
		return totalFines;
	}

	public void setTotalFines(long totalFines) {
		this.totalFines = totalFines;
	}

	public long getOpenFines() {
		return openFines;
	}

	public void setOpenFines(long openFines) {
		this.openFines = openFines;
	}

	public long getTotalFineAmountCents() {
		return totalFineAmountCents;
	}

	public void setTotalFineAmountCents(long totalFineAmountCents) {
		this.totalFineAmountCents = totalFineAmountCents;
	}

	public long getUsersWithDebt() {
		return usersWithDebt;
	}

	public void setUsersWithDebt(long usersWithDebt) {
		this.usersWithDebt = usersWithDebt;
	}
}

