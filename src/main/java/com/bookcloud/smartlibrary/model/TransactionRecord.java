package com.bookcloud.smartlibrary.model;

import java.time.Instant;

import com.bookcloud.smartlibrary.enums.ReservationTransactionPhase;
import com.bookcloud.smartlibrary.enums.TransactionRecordType;
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
@Table(name = "transaction_records")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TransactionRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionRecordType type;

	@Enumerated(EnumType.STRING)
	private ReservationTransactionPhase reservationPhase;

	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant occurredAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private AppUser user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	@JsonIgnore
	private Book book;

	private Long referenceId;

	public String getUserUid() {
		return user != null ? user.getUid() : null;
	}

	public Long getBookId() {
		return book != null ? book.getId() : null;
	}
}
