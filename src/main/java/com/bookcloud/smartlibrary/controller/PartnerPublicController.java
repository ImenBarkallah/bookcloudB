package com.bookcloud.smartlibrary.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.model.Partner;
import com.bookcloud.smartlibrary.service.PartnerService;

@RestController
@RequestMapping("/api/public/partners")
public class PartnerPublicController {

	private final PartnerService service;

	public PartnerPublicController(PartnerService service) {
		this.service = service;
	}

	@GetMapping
	@PreAuthorize("permitAll()")
	public List<Partner> list() {
		return service.listPublic();
	}
}
