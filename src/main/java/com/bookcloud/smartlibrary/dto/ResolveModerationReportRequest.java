package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public class ResolveModerationReportRequest {
	@NotBlank
	private String action; // RESOLVE | REJECT
	private String note;
	private Boolean hideContent;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getHideContent() {
		return hideContent;
	}

	public void setHideContent(Boolean hideContent) {
		this.hideContent = hideContent;
	}
}

