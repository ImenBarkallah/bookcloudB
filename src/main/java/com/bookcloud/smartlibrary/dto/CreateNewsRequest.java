package com.bookcloud.smartlibrary.dto;

import com.bookcloud.smartlibrary.enums.NewsType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateNewsRequest {
	@NotBlank
	private String title;

	@NotBlank
	private String content;

	@NotNull
	private NewsType type;

	private String imageUrl;
	private boolean active = true;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NewsType getType() {
		return type;
	}

	public void setType(NewsType type) {
		this.type = type;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

