package com.bookcloud.smartlibrary.model;

import java.time.Instant;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "library_settings")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class LibrarySettings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer defaultLoanDays;

	private Integer maxActiveLoansDefault;

	private Integer reservationExpiryDays;

	private Double finePerDay;

	private Integer maxRenewalsPerLoan;

	private Long overdueFinePerDayCents;

	@UpdateTimestamp
	@Column(columnDefinition = "TIMESTAMP", nullable = false)
	private Instant updatedAt;
}
