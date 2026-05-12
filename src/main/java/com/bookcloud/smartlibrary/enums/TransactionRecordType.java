package com.bookcloud.smartlibrary.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Types du journal {@code transactions} (suivi métier simple).
 */
public enum TransactionRecordType {
	@JsonProperty("loan")
	LOAN,
	@JsonProperty("return")
	RETURN,
	@JsonProperty("reservation")
	RESERVATION
}
