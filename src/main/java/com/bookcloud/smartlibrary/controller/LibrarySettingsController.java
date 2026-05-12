package com.bookcloud.smartlibrary.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.UpdateLibrarySettingsRequest;
import com.bookcloud.smartlibrary.model.LibrarySettings;
import com.bookcloud.smartlibrary.service.LibrarySettingsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/settings/library")
public class LibrarySettingsController {

	private final LibrarySettingsService service;

	public LibrarySettingsController(LibrarySettingsService service) {
		this.service = service;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public LibrarySettings get() {
		return service.getOrDefault();
	}

	@PutMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public LibrarySettings update(@Valid @RequestBody UpdateLibrarySettingsRequest req) {
		return service.update(req);
	}
}
