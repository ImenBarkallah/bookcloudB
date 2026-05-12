package com.bookcloud.smartlibrary.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationChannel {
	@JsonProperty("email")
	EMAIL,
	@JsonProperty("in_app")
	IN_APP
}
