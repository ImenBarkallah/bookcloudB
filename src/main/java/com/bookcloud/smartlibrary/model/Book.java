package com.bookcloud.smartlibrary.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "author")
	private String author;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "book_authors",
			joinColumns = @JoinColumn(name = "book_id"),
			inverseJoinColumns = @JoinColumn(name = "author_id"))
	@JsonIgnore
	private Set<Author> authors = new LinkedHashSet<>();

	private String isbn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	@JsonIgnore
	private Category category;

	private String coverUrl;

	private int totalCopies;

	private int availableCopies;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "default_branch_id")
	@JsonIgnore
	private LibraryBranch defaultBranch;

	private Integer publicationYear;

	private String language;

	private String publisher;

	private String mediaFormat;

	private boolean hidden;

	private boolean featured;

	@CreationTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant updatedAt;

	public Long getAuthorId() {
		return authors.stream().findFirst().map(Author::getId).orElse(null);
	}

	public List<Long> getAuthorIds() {
		List<Long> ids = new ArrayList<>();
		for (Author authorItem : authors) {
			if (authorItem.getId() != null) {
				ids.add(authorItem.getId());
			}
		}
		return ids;
	}

	public Long getCategoryId() {
		return category != null ? category.getId() : null;
	}

	public Long getDefaultBranchId() {
		return defaultBranch != null ? defaultBranch.getId() : null;
	}
}
