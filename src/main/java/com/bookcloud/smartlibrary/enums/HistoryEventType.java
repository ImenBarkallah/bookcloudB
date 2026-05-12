package com.bookcloud.smartlibrary.enums;

/** Types d’événements pour la chronologie utilisateur (collection {@code libraryHistory}). */
public enum HistoryEventType {
	LOAN_BORROWED,
	LOAN_RETURNED,
	RESERVATION_CREATED,
	RESERVATION_CANCELLED,
	RESERVATION_APPROVED,
	RESERVATION_REJECTED,
	RESERVATION_EXPIRED,
	RESERVATION_CONVERTED_TO_LOAN
}
