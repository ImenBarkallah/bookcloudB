package com.bookcloud.smartlibrary.dto;

public class AdminCategoryCountDto {
	private Long categoryId;
	private String categoryName;
	private long books;

	public AdminCategoryCountDto() {
	}

	public AdminCategoryCountDto(Long categoryId, String categoryName, long books) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.books = books;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public long getBooks() {
		return books;
	}

	public void setBooks(long books) {
		this.books = books;
	}
}
