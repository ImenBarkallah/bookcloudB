package com.bookcloud.smartlibrary.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bookcloud.smartlibrary.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonGetter;
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
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Reservation {

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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status = ReservationStatus.PENDING;

	@CreationTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pickup_branch_id")
	@JsonIgnore
	private LibraryBranch pickupBranch;

	@Column(columnDefinition = "TIMESTAMP")
	private Instant expiresAt;

	private Integer queuePosition;

	public Long getBookId() {
		return book != null ? book.getId() : null;
	}

	public String getUserUid() {
		return user != null ? user.getUid() : null;
	}

	public Long getPickupBranchId() {
		return pickupBranch != null ? pickupBranch.getId() : null;
	}

	@JsonGetter("reservedAt")
	public Instant getReservedAt() {
		return createdAt;
	}
}
