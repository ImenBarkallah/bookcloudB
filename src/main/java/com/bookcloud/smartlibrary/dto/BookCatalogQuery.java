package com.bookcloud.smartlibrary.dto;

/** Query parameters for GET /api/books/catalog/paged */
public class BookCatalogQuery {

	private int page = 0;
	private int size = 12;
	private String search;
	private Long categoryId;
	private String language;
	private Integer yearFrom;
	private Integer yearTo;
	private Double minRating;
	private Boolean availableOnly;
	/** title | year | rating | newest */
	private String sort = "newest";
	/** PHYSICAL | EBOOK | AUDIO */
	private String format;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(Integer yearTo) {
		this.yearTo = yearTo;
	}

	public Double getMinRating() {
		return minRating;
	}

	public void setMinRating(Double minRating) {
		this.minRating = minRating;
	}

	public Boolean getAvailableOnly() {
		return availableOnly;
	}

	public void setAvailableOnly(Boolean availableOnly) {
		this.availableOnly = availableOnly;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}

