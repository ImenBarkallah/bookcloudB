package com.bookcloud.smartlibrary.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bookcloud.smartlibrary.enums.OfferType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Offer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	private String imageUrl;

	@Enumerated(EnumType.STRING)
	private OfferType type;

	private LocalDate startDate;

	private LocalDate endDate;

	private boolean active;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "offer_related_books",
			joinColumns = @JoinColumn(name = "offer_id"),
			inverseJoinColumns = @JoinColumn(name = "book_id"))
	@JsonIgnore
	private Set<Book> relatedBooks = new LinkedHashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "offer_categories",
			joinColumns = @JoinColumn(name = "offer_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id"))
	@JsonIgnore
	private Set<Category> categories = new LinkedHashSet<>();

	@CreationTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant updatedAt;

	public List<Long> getRelatedBookIds() {
		List<Long> ids = new ArrayList<>();
		for (Book book : relatedBooks) {
			if (book.getId() != null) {
				ids.add(book.getId());
			}
		}
		return ids;
	}

	public List<Long> getCategoryIds() {
		List<Long> ids = new ArrayList<>();
		for (Category category : categories) {
			if (category.getId() != null) {
				ids.add(category.getId());
			}
		}
		return ids;
	}
}
