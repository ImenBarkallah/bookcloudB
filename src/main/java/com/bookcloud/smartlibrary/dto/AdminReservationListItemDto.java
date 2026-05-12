package com.bookcloud.smartlibrary.dto;

import java.time.Instant;

/** Liste admin des réservations : livre + usager enrichis pour l’UI Kanban. */
public class AdminReservationListItemDto {

	private Long id;
	private Long bookId;
	private String bookTitle;
	private String bookAuthor;
	private String coverUrl;
	private String userUid;
	private String userDisplayName;
	private String userEmail;
	private Instant reservedAt;
	private Instant expiresAt;
	private Integer queuePosition;
	private String status;
	private Long pickupBranchId;

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

	public Instant getReservedAt() {
		return reservedAt;
	}

	public void setReservedAt(Instant reservedAt) {
		this.reservedAt = reservedAt;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Integer getQueuePosition() {
		return queuePosition;
	}

	public void setQueuePosition(Integer queuePosition) {
		this.queuePosition = queuePosition;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getPickupBranchId() {
		return pickupBranchId;
	}

	public void setPickupBranchId(Long pickupBranchId) {
		this.pickupBranchId = pickupBranchId;
	}
}


