package com.bookcloud.smartlibrary.dto;

import java.util.ArrayList;
import java.util.List;

public class BookCatalogFiltersDto {

	private List<CatalogFilterOptionDto> categories = new ArrayList<>();
	private List<String> formats = new ArrayList<>();
	private List<String> languages = new ArrayList<>();
	private Integer minPublicationYear;
	private Integer maxPublicationYear;

	public List<CatalogFilterOptionDto> getCategories() {
		return categories;
	}

	public void setCategories(List<CatalogFilterOptionDto> categories) {
		this.categories = categories;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> formats) {
		this.formats = formats;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	public Integer getMinPublicationYear() {
		return minPublicationYear;
	}

	public void setMinPublicationYear(Integer minPublicationYear) {
		this.minPublicationYear = minPublicationYear;
	}

	public Integer getMaxPublicationYear() {
		return maxPublicationYear;
	}

	public void setMaxPublicationYear(Integer maxPublicationYear) {
		this.maxPublicationYear = maxPublicationYear;
	}
}
