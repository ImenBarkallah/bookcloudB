package com.bookcloud.smartlibrary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreateModerationReportRequest;
import com.bookcloud.smartlibrary.dto.ResolveModerationReportRequest;
import com.bookcloud.smartlibrary.model.ModerationReport;
import com.bookcloud.smartlibrary.service.ModerationService;
import com.bookcloud.smartlibrary.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

	private final ModerationService service;

	public ModerationController(ModerationService service) {
		this.service = service;
	}

	@PostMapping("/reports")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public Map<String, Long> report(@Valid @RequestBody CreateModerationReportRequest req,
			Authentication authentication) {
		Long id = service.report(req, SecurityUtils.uid(authentication));
		return Map.of("id", id);
	}

	@GetMapping("/reports")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<ModerationReport> list(@RequestParam(required = false) String status) {
		return service.list(status);
	}

	@PutMapping("/reports/{id}/resolve")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ModerationReport resolve(@PathVariable Long id, @Valid @RequestBody ResolveModerationReportRequest req,
			Authentication authentication) {
		return service.resolve(id, req, SecurityUtils.uid(authentication));
	}

	@PutMapping("/content/{type}/{id}/hidden/{hidden}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public void setHidden(@PathVariable String type, @PathVariable Long id, @PathVariable boolean hidden) {
		service.setHidden(type, id, hidden);
	}
}
