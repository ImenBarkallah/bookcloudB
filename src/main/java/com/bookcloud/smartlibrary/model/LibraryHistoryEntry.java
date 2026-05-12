package com.bookcloud.smartlibrary.model;

import java.time.Instant;

import com.bookcloud.smartlibrary.enums.HistoryEventType;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "library_history_entries")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class LibraryHistoryEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private AppUser user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private HistoryEventType type;

	private Long referenceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	@JsonIgnore
	private Book book;

	@Column(columnDefinition = "TEXT")
	private String summary;

	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant occurredAt;

	public String getUserUid() {
		return user != null ? user.getUid() : null;
	}

	public Long getBookId() {
		return book != null ? book.getId() : null;
	}
}
