package com.bookcloud.smartlibrary.dto;

public class PatchNotificationReadRequest {

	private boolean read = true;

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
}
