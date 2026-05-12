package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.NotNull;

public class CreateLoanRequest {

	@NotNull
	private Long bookId;
	/** Antenne préférée pour choisir un exemplaire localisé. */
	private Long branchId;
	/** Exemplaire précis (personnel ou utilisateur autorisé). */
	private Long copyId;

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
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
}

