package com.bookcloud.smartlibrary.dto;

import com.bookcloud.smartlibrary.enums.NotificationChannel;

import jakarta.validation.constraints.NotBlank;

public class CreateAppNotificationRequest {

	@NotBlank
	private String userUid;
	private NotificationChannel channel = NotificationChannel.IN_APP;
	@NotBlank
	private String message;

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public NotificationChannel getChannel() {
		return channel;
	}

	public void setChannel(NotificationChannel channel) {
		this.channel = channel;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
