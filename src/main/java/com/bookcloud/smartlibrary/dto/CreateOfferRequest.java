package com.bookcloud.smartlibrary.dto;

import java.time.LocalDate;
import java.util.List;

import com.bookcloud.smartlibrary.enums.OfferType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateOfferRequest {
	@NotBlank
	private String title;
	private String description;
	private String imageUrl;
	@NotNull
	private OfferType type;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean active;

	/** BOOK offers. */
	private List<Long> relatedBookIds;
	/** CATEGORY offers. */
	private List<Long> categoryIds;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public OfferType getType() {
		return type;
	}

	public void setType(OfferType type) {
		this.type = type;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Long> getRelatedBookIds() {
		return relatedBookIds;
	}

	public void setRelatedBookIds(List<Long> relatedBookIds) {
		this.relatedBookIds = relatedBookIds;
	}

	public List<Long> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}
}


