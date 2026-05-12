package com.bookcloud.smartlibrary.dto;

public class FavoriteToggleResponse {

	private Long bookId;
	private boolean favorited;

	public FavoriteToggleResponse() {
	}

	public FavoriteToggleResponse(Long bookId, boolean favorited) {
		this.bookId = bookId;
		this.favorited = favorited;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}
}

