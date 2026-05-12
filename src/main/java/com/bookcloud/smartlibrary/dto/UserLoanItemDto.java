package com.bookcloud.smartlibrary.dto;

import java.time.Instant;

/**
 * Prêt + métadonnées livre pour l’écran « Mes emprunts ».
 */
public class UserLoanItemDto {

	private Long loanId;
	private Long bookId;
	private String bookTitle;
	private String bookAuthor;
	private String coverUrl;
	/** ACTIVE, RETURNED, OVERDUE */
	private String status;
	private Instant borrowedAt;
	private Instant dueAt;
	private Instant returnedAt;
	private int renewalCount;
	private int maxRenewals;
	private Long branchId;
	private boolean canRenew;
	private boolean canReturn;

	public UserLoanItemDto() {
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getBorrowedAt() {
		return borrowedAt;
	}

	public void setBorrowedAt(Instant borrowedAt) {
		this.borrowedAt = borrowedAt;
	}

	public Instant getDueAt() {
		return dueAt;
	}

	public void setDueAt(Instant dueAt) {
		this.dueAt = dueAt;
	}

	public Instant getReturnedAt() {
		return returnedAt;
	}

	public void setReturnedAt(Instant returnedAt) {
		this.returnedAt = returnedAt;
	}

	public int getRenewalCount() {
		return renewalCount;
	}

	public void setRenewalCount(int renewalCount) {
		this.renewalCount = renewalCount;
	}

	public int getMaxRenewals() {
		return maxRenewals;
	}

	public void setMaxRenewals(int maxRenewals) {
		this.maxRenewals = maxRenewals;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public boolean isCanRenew() {
		return canRenew;
	}

	public void setCanRenew(boolean canRenew) {
		this.canRenew = canRenew;
	}

	public boolean isCanReturn() {
		return canReturn;
	}

	public void setCanReturn(boolean canReturn) {
		this.canReturn = canReturn;
	}
}

