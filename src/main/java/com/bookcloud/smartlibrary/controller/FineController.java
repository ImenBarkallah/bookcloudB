package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.model.Fine;
import com.bookcloud.smartlibrary.service.FineService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

@RestController
@RequestMapping("/api/fines")
public class FineController {

	private final FineService fineService;

	public FineController(FineService fineService) {
		this.fineService = fineService;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public List<Fine> mine(Authentication authentication) {
		return fineService.listMine(SecurityUtils.uid(authentication));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<Fine> listAll(Authentication authentication) {
		return fineService.listAll(SecurityUtils.currentRole(authentication));
	}

	@PostMapping("/{id}/pay")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public Fine markPaid(@PathVariable Long id, Authentication authentication) {
		return fineService.markPaid(id, SecurityUtils.currentRole(authentication));
	}
}
