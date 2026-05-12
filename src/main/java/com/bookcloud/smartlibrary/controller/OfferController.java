package com.bookcloud.smartlibrary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreateOfferRequest;
import com.bookcloud.smartlibrary.dto.OfferAdminQuery;
import com.bookcloud.smartlibrary.dto.OfferDto;
import com.bookcloud.smartlibrary.model.Offer;
import com.bookcloud.smartlibrary.service.OfferService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

	private final OfferService service;

	public OfferController(OfferService service) {
		this.service = service;
	}

	@GetMapping
	@PreAuthorize("permitAll()")
	public List<OfferDto> active() {
		return service.listActiveForUsers();
	}

	@GetMapping("/recommended/{userId}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','USER')")
	public List<OfferDto> recommended(@PathVariable String userId,
			@RequestParam(defaultValue = "10") int limit) {
		return service.recommendedForUser(userId, limit);
	}

	@GetMapping("/{id}")
	@PreAuthorize("permitAll()")
	public OfferDto get(@PathVariable Long id) {
		return service.getByIdForUser(id);
	}

	@GetMapping("/admin")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<OfferDto> adminList(OfferAdminQuery query) {
		return service.listAllAdmin(query);
	}

	@GetMapping("/admin/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public OfferDto adminGet(@PathVariable Long id) {
		return service.getByIdForAdmin(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Long>> create(@Valid @RequestBody CreateOfferRequest req) {
		Long id = service.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public Offer update(@PathVariable Long id, @Valid @RequestBody CreateOfferRequest req) {
		return service.update(id, req);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
