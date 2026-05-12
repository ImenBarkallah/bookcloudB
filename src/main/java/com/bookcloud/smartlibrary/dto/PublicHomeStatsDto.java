package com.bookcloud.smartlibrary.dto;

/** Statistiques publiques légères (Home). */
public class PublicHomeStatsDto {
	private long books;
	private long members;

	public PublicHomeStatsDto() {
	}

	public PublicHomeStatsDto(long books, long members) {
		this.books = books;
		this.members = members;
	}

	public long getBooks() {
		return books;
	}

	public void setBooks(long books) {
		this.books = books;
	}

	public long getMembers() {
		return members;
	}

	public void setMembers(long members) {
		this.members = members;
	}
}

