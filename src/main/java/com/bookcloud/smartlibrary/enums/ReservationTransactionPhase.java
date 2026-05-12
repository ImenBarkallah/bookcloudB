package com.bookcloud.smartlibrary.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Précision pour les entrées {@link TransactionRecordType#RESERVATION} du journal.
 */
public enum ReservationTransactionPhase {
	@JsonProperty("created")
	CREATED,
	@JsonProperty("cancelled")
	CANCELLED,
	@JsonProperty("approved")
	APPROVED,
	@JsonProperty("rejected")
	REJECTED,
	@JsonProperty("expired")
	EXPIRED,
	@JsonProperty("converted")
	CONVERTED
}
