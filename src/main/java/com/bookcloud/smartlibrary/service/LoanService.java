package com.bookcloud.smartlibrary.service;

import java.util.List;

import com.bookcloud.smartlibrary.dto.AdminLoanDetailDto;
import com.bookcloud.smartlibrary.dto.AdminLoanListItemDto;
import com.bookcloud.smartlibrary.dto.UserLoanItemDto;
import com.bookcloud.smartlibrary.enums.Role;
import com.bookcloud.smartlibrary.model.Loan;

public interface LoanService {
	Loan borrow(Long bookId, String userUid, Long branchId, Long copyId);
	Loan renewLoan(Long loanId, String actingUid, Role callerRole);
	Loan returnLoan(Long loanId, String actingUid, Role callerRole);
	Loan getByIdForCaller(Long loanId, String actingUid, Role role);
	void deleteByStaff(Long loanId, Role role);
	List<Loan> listForUser(String userUid);
	List<UserLoanItemDto> listUserLoanItems(String userUid);
	List<Loan> listAll();
	List<AdminLoanListItemDto> listAllForAdmin();
	AdminLoanDetailDto getAdminLoanDetail(Long loanId, String actingUid, Role role);
}
