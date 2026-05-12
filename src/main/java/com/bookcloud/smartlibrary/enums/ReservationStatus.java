package com.bookcloud.smartlibrary.enums;

public enum ReservationStatus {
	/** En attente dans la file. */
	PENDING,
	/** Validée par le personnel (prête à être convertie en prêt). */
	APPROVED,
	/** Annulée / rejetée. */
	CANCELLED,
	/** Expirée (date limite dépassée). */
	EXPIRED
}
