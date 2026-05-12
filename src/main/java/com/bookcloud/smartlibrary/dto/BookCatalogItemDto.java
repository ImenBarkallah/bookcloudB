package com.bookcloud.smartlibrary.dto;

public class BookCatalogItemDto {

	private Long id;
	private String title;
	private String author;
	private Long categoryId;
	private String categoryName;
	private String coverUrl;
	private Integer publicationYear;
	private String language;
	private String publisher;
	private String isbn;
	private int totalCopies;
	private int availableCopies;
	private boolean isNew;
	private boolean isPopular;
	private double averageRating;
	private int ratingCount;
	private Boolean isFavorited;
	private String mediaFormat;
	private boolean featured;

	public BookCatalogItemDto() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public int getTotalCopies() {
		return totalCopies;
	}

	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}

	public int getAvailableCopies() {
		return availableCopies;
	}

	public void setAvailableCopies(int availableCopies) {
		this.availableCopies = availableCopies;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean aNew) {
		isNew = aNew;
	}

	public boolean isPopular() {
		return isPopular;
	}

	public void setPopular(boolean popular) {
		isPopular = popular;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public int getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Boolean getIsFavorited() {
		return isFavorited;
	}

	public void setIsFavorited(Boolean favorited) {
		isFavorited = favorited;
	}

	public String getMediaFormat() {
		return mediaFormat;
	}

	public void setMediaFormat(String mediaFormat) {
		this.mediaFormat = mediaFormat;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
}

