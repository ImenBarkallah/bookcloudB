package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.NotNull;

public class FavoriteToggleRequest {

	@NotNull(message = "bookId requis")
	private Long bookId;

	public FavoriteToggleRequest() {
	}

	public FavoriteToggleRequest(Long bookId) {
		this.bookId = bookId;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
}
