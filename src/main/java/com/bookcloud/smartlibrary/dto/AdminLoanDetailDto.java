package com.bookcloud.smartlibrary.dto;

import java.util.List;

public class AdminLoanDetailDto extends AdminLoanListItemDto {

	private List<LoanHistoryLineDto> history;

	public List<LoanHistoryLineDto> getHistory() {
		return history;
	}

	public void setHistory(List<LoanHistoryLineDto> history) {
		this.history = history;
	}
}
