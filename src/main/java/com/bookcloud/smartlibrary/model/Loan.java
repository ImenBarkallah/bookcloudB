package com.bookcloud.smartlibrary.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bookcloud.smartlibrary.enums.LoanStatus;
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
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	@JsonIgnore
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private AppUser user;

	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant borrowedAt;

	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant dueAt;

	@Column(columnDefinition = "TIMESTAMP")
	private Instant returnedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LoanStatus status = LoanStatus.ACTIVE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id")
	@JsonIgnore
	private LibraryBranch branch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "copy_id")
	@JsonIgnore
	private BookCopy copy;

	private int renewalCount;

	@CreationTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant updatedAt;

	public Long getBookId() {
		return book != null ? book.getId() : null;
	}

	public String getUserUid() {
		return user != null ? user.getUid() : null;
	}

	public Long getBranchId() {
		return branch != null ? branch.getId() : null;
	}

	public Long getCopyId() {
		return copy != null ? copy.getId() : null;
	}
}
