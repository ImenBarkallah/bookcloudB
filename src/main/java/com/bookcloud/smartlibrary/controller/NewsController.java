package com.bookcloud.smartlibrary.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bookcloud.smartlibrary.dto.CreateNewsRequest;
import com.bookcloud.smartlibrary.dto.NewsDto;
import com.bookcloud.smartlibrary.dto.UpdateNewsRequest;
import com.bookcloud.smartlibrary.enums.NewsType;
import com.bookcloud.smartlibrary.service.NewsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/news")
public class NewsController {

	private final NewsService service;

	public NewsController(NewsService service) {
		this.service = service;
	}

	@GetMapping
	@PreAuthorize("permitAll()")
	public List<NewsDto> list(@RequestParam(required = false) String type) {
		return service.listActive(resolveType(type));
	}

	@GetMapping("/{id}")
	@PreAuthorize("permitAll()")
	public NewsDto get(@PathVariable Long id) {
		return service.getPublic(id);
	}

	@GetMapping("/admin")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public List<NewsDto> adminList(@RequestParam(required = false) Boolean active,
			@RequestParam(required = false) String type) {
		return service.listAdmin(active, resolveType(type));
	}

	@GetMapping("/admin/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
	public NewsDto adminGet(@PathVariable Long id) {
		return service.getAdmin(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Long>> create(@Valid @RequestBody CreateNewsRequest req) {
		Long id = service.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public NewsDto update(@PathVariable Long id, @RequestBody UpdateNewsRequest req) {
		return service.update(id, req);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public NewsDto uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
		return service.uploadImage(id, file);
	}

	private static NewsType resolveType(String raw) {
		if (raw == null) {
			return null;
		}
		String normalized = raw.trim();
		if (normalized.isEmpty() || "ALL".equalsIgnoreCase(normalized)) {
			return null;
		}
		return NewsType.valueOf(normalized.toUpperCase(Locale.ROOT));
	}
}
