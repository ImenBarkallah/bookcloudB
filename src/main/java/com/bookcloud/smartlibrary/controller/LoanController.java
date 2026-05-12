package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.AdminLoanDetailDto;
import com.bookcloud.smartlibrary.dto.AdminLoanListItemDto;
import com.bookcloud.smartlibrary.dto.CreateLoanRequest;
import com.bookcloud.smartlibrary.dto.UserLoanItemDto;
import com.bookcloud.smartlibrary.model.Loan;
import com.bookcloud.smartlibrary.service.LoanService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

	private final LoanService loanService;

	public LoanController(LoanService loanService) {
		this.loanService = loanService;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public List<UserLoanItemDto> myLoans(Authentication authentication) {
		return loanService.listUserLoanItems(SecurityUtils.uid(authentication));
	}

	@GetMapping("/{id}/admin-detail")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public AdminLoanDetailDto adminLoanDetail(@PathVariable Long id, Authentication authentication) {
		return loanService.getAdminLoanDetail(id, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Loan getById(@PathVariable Long id, Authentication authentication) {
		return loanService.getByIdForCaller(id, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<AdminLoanListItemDto> listAll() {
		return loanService.listAllForAdmin();
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Loan borrow(@Valid @RequestBody CreateLoanRequest req, Authentication authentication) {
		return loanService.borrow(req.getBookId(), SecurityUtils.uid(authentication), req.getBranchId(),
				req.getCopyId());
	}

	@PostMapping("/{id}/renew")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Loan renew(@PathVariable Long id, Authentication authentication) {
		return loanService.renewLoan(id, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}

	@PostMapping("/{id}/return")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Loan returnLoan(@PathVariable Long id, Authentication authentication) {
		return loanService.returnLoan(id, SecurityUtils.uid(authentication),
				SecurityUtils.currentRole(authentication));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
		loanService.deleteByStaff(id, SecurityUtils.currentRole(authentication));
		return ResponseEntity.noContent().build();
	}
}
