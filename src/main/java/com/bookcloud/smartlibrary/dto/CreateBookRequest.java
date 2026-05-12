package com.bookcloud.smartlibrary.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class CreateBookRequest {

	@NotBlank
	private String title;

	private String description;

	@NotEmpty
	private List<Long> authorIds;

	private String isbn;

	@NotNull
	private Long categoryId;

	@Min(1)
	private int totalCopies;

	private MultipartFile cover;
	private Long defaultBranchId;
	private Integer publicationYear;
	private String language;
	private String publisher;
	private boolean featured;

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

	public int getTotalCopies() {
		return totalCopies;
	}

	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}

	public MultipartFile getCover() {
		return cover;
	}

	public void setCover(MultipartFile cover) {
		this.cover = cover;
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

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
}
