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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookcloud.smartlibrary.dto.CreateAuthorRequest;
import com.bookcloud.smartlibrary.dto.UpdateAuthorRequest;
import com.bookcloud.smartlibrary.model.Author;
import com.bookcloud.smartlibrary.service.AuthorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

	private final AuthorService authorService;

	public AuthorController(AuthorService authorService) {
		this.authorService = authorService;
	}

	@GetMapping
	@PreAuthorize("permitAll()")
	public List<Author> list() {
		return authorService.listAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("permitAll()")
	public Author get(@PathVariable Long id) {
		return authorService.getById(id);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Map<String, Long>> create(@Valid @ModelAttribute CreateAuthorRequest req) {
		Long id = authorService.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> update(
			@PathVariable Long id,
			@Valid @ModelAttribute UpdateAuthorRequest req) {
		authorService.update(id, req);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		authorService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
