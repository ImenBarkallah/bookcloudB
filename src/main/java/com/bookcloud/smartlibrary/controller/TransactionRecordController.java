package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.model.TransactionRecord;
import com.bookcloud.smartlibrary.service.TransactionRecordQueryService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRecordController {

	private final TransactionRecordQueryService transactionRecordQueryService;

	public TransactionRecordController(TransactionRecordQueryService transactionRecordQueryService) {
		this.transactionRecordQueryService = transactionRecordQueryService;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public List<TransactionRecord> mine(Authentication authentication) {
		return transactionRecordQueryService.listForUser(SecurityUtils.uid(authentication));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<TransactionRecord> listAll(Authentication authentication) {
		return transactionRecordQueryService.listAll(SecurityUtils.currentRole(authentication));
	}
}
