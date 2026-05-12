package com.bookcloud.smartlibrary.dto;

import com.bookcloud.smartlibrary.enums.NewsType;

public class UpdateNewsRequest {
	private String title;
	private String content;
	private NewsType type;
	private String imageUrl;
	private Boolean active;

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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}

