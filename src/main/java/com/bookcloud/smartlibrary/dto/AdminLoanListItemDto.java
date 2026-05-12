package com.bookcloud.smartlibrary.dto;

import java.time.Instant;

/** Liste admin des emprunts : livre + usager enrichis pour l’UI. */
public class AdminLoanListItemDto {

	private Long id;
	private Long bookId;
	private String bookTitle;
	private String bookAuthor;
	private String coverUrl;
	private String userUid;
	private String userDisplayName;
	private String userEmail;
	private Instant borrowedAt;
	private Instant dueAt;
	private Instant returnedAt;
	private String status;
	private Long branchId;
	private Long copyId;
	private int renewalCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getCopyId() {
		return copyId;
	}

	public void setCopyId(Long copyId) {
		this.copyId = copyId;
	}

	public int getRenewalCount() {
		return renewalCount;
	}

	public void setRenewalCount(int renewalCount) {
		this.renewalCount = renewalCount;
	}
}

