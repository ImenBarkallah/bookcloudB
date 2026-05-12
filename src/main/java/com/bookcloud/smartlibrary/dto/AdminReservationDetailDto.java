package com.bookcloud.smartlibrary.dto;

import java.util.List;

public class AdminReservationDetailDto extends AdminReservationListItemDto {

	private List<ReservationHistoryLineDto> history;

	public List<ReservationHistoryLineDto> getHistory() {
		return history;
	}

	public void setHistory(List<ReservationHistoryLineDto> history) {
		this.history = history;
	}
}

