package com.bookcloud.smartlibrary.dto;

import jakarta.validation.constraints.Min;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class UpdateBookRequest {

	private String title;
	private String description;
	private List<Long> authorIds;
	private String isbn;
	private Long categoryId;
	private MultipartFile cover;

	@Min(0)
	private Integer totalCopies;

	private Long defaultBranchId;
	private Integer publicationYear;
	private String language;
	private String publisher;
	/** Mis en avant sur la home (max 5). */
	private Boolean featured;

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

	public List<Long> getAuthorIds() {
		return authorIds;
	}

	public void setAuthorIds(List<Long> authorIds) {
		this.authorIds = authorIds;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public MultipartFile getCover() {
		return cover;
	}

	public void setCover(MultipartFile cover) {
		this.cover = cover;
	}

	public Integer getTotalCopies() {
		return totalCopies;
	}

	public void setTotalCopies(Integer totalCopies) {
		this.totalCopies = totalCopies;
	}

	public Long getDefaultBranchId() {
		return defaultBranchId;
	}

	public void setDefaultBranchId(Long defaultBranchId) {
		this.defaultBranchId = defaultBranchId;
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

	public Boolean getFeatured() {
		return featured;
	}

	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}
}

