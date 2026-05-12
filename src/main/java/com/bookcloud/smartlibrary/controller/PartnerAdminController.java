package com.bookcloud.smartlibrary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreatePartnerRequest;
import com.bookcloud.smartlibrary.dto.PagedPartnersResponse;
import com.bookcloud.smartlibrary.dto.UpdatePartnerRequest;
import com.bookcloud.smartlibrary.model.Partner;
import com.bookcloud.smartlibrary.service.PartnerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/partners")
public class PartnerAdminController {

	private final PartnerService service;

	public PartnerAdminController(PartnerService service) {
		this.service = service;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<Partner> list() {
		return service.listAdmin();
	}

	@GetMapping("/paged")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public PagedPartnersResponse listPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) Boolean includeArchived) {
		return service.listAdminPaged(page, size, search, includeArchived);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public Partner get(@PathVariable Long id) {
		return service.get(id);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Map<String, Long>> create(@Valid @ModelAttribute CreatePartnerRequest req) {
		Long id = service.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> update(@PathVariable Long id, @Valid @ModelAttribute UpdatePartnerRequest req) {
		service.update(id, req);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/archive")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> archive(@PathVariable Long id, @RequestParam boolean archived) {
		service.setArchived(id, archived);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
